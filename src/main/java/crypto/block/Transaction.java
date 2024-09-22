package crypto.block;

import crypto.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record Transaction(String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements Request<Transaction> {

    @Override
    public void mine(String id, BlockData<Transaction> blockData) {
        if (!verify(id, blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        for (Transaction request : blockData.data()) {
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                Caches.addAccount(id, transactionOutput.getRecipient(), request.currency(), transactionOutput.getValue());
                Caches.addAccount(id, request.publicKey(), request.currency(), -transactionOutput.getValue());
            }
        }
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public BlockData<Transaction> prepare(String id, List<Transaction> requests) {
        List<Transaction> selected = new ArrayList<>();
        for (Transaction request : requests) {
            if (!verify(id, request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(String id, Transaction request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = generateHash(request.publicKey(), request.currency(), request.transactionOutputs(), Caches.getHashType(id));
            if (!ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()))) return false;
        } catch (GeneralSecurityException e){
            return false;
        }

        if (Caches.hasAccountCache(id, request.currency())) {
            long sum = 0L;
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                sum += transactionOutput.getValue();
            }
            Long balance = Caches.getAccount(id, request.currency(), request.publicKey());
            if (!(balance >= sum)) return false;
        }
        return true;
    }

    @Override
    public String getPreHash() { //and the prehash would be the signature for others also?
        return signature;
    }

    public static Transaction create(String id, String from, String to, String currency, Long value) throws ChainException {
        Keypair keypair = AuxData.getKeypair(from);
        if (keypair == null) return null;
        if (!Caches.getCurrency(id, currency).key().equals(keypair.publicKey())){
            Long balance = Caches.getAccount(id, currency, keypair.publicKey());
            if (balance < value) return null;
        }
        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(to, currency, value));
        return create(id, keypair, currency, transactionOutputs);
    }

    private static Transaction create(String id, Keypair keypair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String hash = generateHash(keypair.publicKey(), currency, transactionOutputs, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Transaction(keypair.publicKey(), currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }

    public static String generateHash(String publicKey, String currency, List<TransactionOutput> transactionOutputs, Hashing.Type hashType) {
        String preHash = publicKey + "~" + currency + "~" +
                String.join("@", transactionOutputs.stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }

}

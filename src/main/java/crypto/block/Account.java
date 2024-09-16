package crypto.block;

import crypto.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record Account(String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements Request<Account> {

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String publicKey, String currency, List<TransactionOutput> transactionOutputs) {
        String preHash = publicKey + "~" + currency + "~" +
                String.join("@", transactionOutputs.stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    @Override
    public void mine(String id, BlockData<Account> blockData) {
        if (!verify(id, blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        for (Account request : blockData.data()) {
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                Caches.addAccount(id, transactionOutput.getRecipient(), request.currency(), transactionOutput.getValue());
                Caches.addAccount(id, request.publicKey(), request.currency(), -transactionOutput.getValue());
            }
        }
        Requests.remove(id, blockData.data(), BlockType.ACCOUNT);
    }


    @Override
    public BlockData<Account> prepare(String id, List<Account> requests) {
        List<Account> selected = new ArrayList<>();
        for (Account request : requests) {
            if (!verify(id, request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(String id, Account request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = Account.generateHash(request.publicKey(), request.currency(), request.transactionOutputs());
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

    public static Account create(String id, String from, String to, String currency, Long value) throws ChainException {
        Keypair keypair = AuxData.getKeypair(from);
        if (keypair == null) return null;
        if (!Caches.getCurrency(id, currency).publicKey().equals(keypair.publicKey())){
            Long balance = Caches.getAccount(id, currency, keypair.publicKey());
            if (balance < value) return null;
        }
        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(to, currency, value));
        return create(keypair, currency, transactionOutputs);
    }

    private static Account create(Keypair keypair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String hash = Account.generateHash(keypair.publicKey(), currency, transactionOutputs);
        byte[] signature = Signing.sign(keypair, hash);
        return new Account(keypair.publicKey(), currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }


}

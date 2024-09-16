package crypto.block.account;

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

public record AccountRequest(String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements Request<AccountRequest> {

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
    public void mine(String id, BlockData<AccountRequest> blockData) {
        if (!verify(id, blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        for (AccountRequest request : blockData.data()) {
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                Data.addAccount(id, transactionOutput.getRecipient(), request.currency(), transactionOutput.getValue());
                Data.addAccount(id, request.publicKey(), request.currency(), -transactionOutput.getValue());
            }
        }
        Requests.remove(id, blockData.data(), BlockType.ACCOUNT);
    }


    @Override
    public BlockData<AccountRequest> prepare(String id, List<AccountRequest> requests) {
        List<AccountRequest> selected = new ArrayList<>();
        for (AccountRequest request : requests) {
            if (!verify(id, request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(String id, AccountRequest request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = AccountRequest.generateHash(request.publicKey(), request.currency(), request.transactionOutputs());
            if (!ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()))) return false;
        } catch (GeneralSecurityException e){
            return false;
        }

        if (Data.hasAccountCache(id, request.currency())) {
            long sum = 0L;
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                sum += transactionOutput.getValue();
            }
            Long balance = Data.getAccount(id, request.currency(), request.publicKey());
            if (!(balance >= sum)) return false;
        }
        return true;
    }

    public static AccountRequest create(String id, String from, String to, String currency, Long value) throws ChainException {
        Keypair keypair = AuxData.getKeypair(from);
        if (keypair == null) return null;
        if (!Data.getCurrency(id, currency).publicKey().equals(keypair.publicKey())){
            Long balance = Data.getAccount(id, currency, keypair.publicKey());
            if (balance < value) return null;
        }
        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(to, currency, value));
        return create(keypair, currency, transactionOutputs);
    }

    private static AccountRequest create(Keypair keypair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String hash = AccountRequest.generateHash(keypair.publicKey(), currency, transactionOutputs);
        byte[] signature = Signing.sign(keypair, hash);
        return new AccountRequest(keypair.publicKey(), currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }


}

package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public record AccountRequest(String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements BlockDataHashable, Request {

    @Override
    public String getBlockDataHash() {
        byte[] hash = Hashing.hash(signature);
        return Encoder.encodeToHexadecimal(hash);
    }

    public static String generateHash(String publicKey, String currency, List<TransactionOutput> transactionOutputs) {
        String preHash = publicKey + "~" + currency + "~" +
                String.join("@", transactionOutputs.stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

package crypto.block.account;

import crypto.blockchain.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public record AccountRequest(String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements Request {

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

}

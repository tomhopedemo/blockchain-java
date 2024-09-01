package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record AccountTransactionRequest (String publicKey, String currency, List<TransactionOutput> transactionOutputs, String signature) implements BlockDataHashable, Request {

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

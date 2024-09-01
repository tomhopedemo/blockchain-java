package crypto.blockchain.account;

import crypto.blockchain.Data;
import crypto.blockchain.TransactionOutput;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountTransactionVerification {

    public static boolean verify(AccountTransactionRequest transactionRequest, String id) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(transactionRequest.publicKey());
            String hash = AccountTransactionRequest.generateHash(transactionRequest.publicKey(), transactionRequest.currency(), transactionRequest.transactionOutputs());
            boolean verified = ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(transactionRequest.signature()));
            if (!verified){
                return false;
            }
        } catch (GeneralSecurityException e){
            return false;
        }

        if (Data.hasAccountCache(id, transactionRequest.currency())) {
            boolean hasBalance = hasBalance(transactionRequest, id);
            if (!hasBalance) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasBalance(AccountTransactionRequest transactionRequest, String id) {
        long sum = 0L;
        for (TransactionOutput transactionOutput : transactionRequest.transactionOutputs()) {
            sum += transactionOutput.getValue();
        }
        Long balance = Data.getAccountBalance(id, transactionRequest.currency(), transactionRequest.publicKey());
        return balance >= sum;
    }

}

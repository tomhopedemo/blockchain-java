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

    public static boolean verify(AccountTransactionRequest transactionRequest, boolean skipBalanceCheck, String id) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(transactionRequest.publicKeyAddress());
            String integratedHash = transactionRequest.generateIntegratedHash();
            boolean verified = ECDSA.verifyECDSASignature(publicKey, integratedHash.getBytes(UTF_8), Hex.decode(transactionRequest.signature()));
            if (!verified){
                return false;
            }
        } catch (GeneralSecurityException e){
            return false;
        }

        if (!skipBalanceCheck) {
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
        Long balance = Data.getAccountBalance(id, transactionRequest.publicKeyAddress());
        return balance >= sum;
    }

}

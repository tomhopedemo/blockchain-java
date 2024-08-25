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

    public static boolean verifySignature(AccountTransactionRequest transactionRequest, boolean skipEqualityCheck, String id) {
        String transactionOutputsHash = transactionRequest.generateTransactionOutputsHash();
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(transactionRequest.getPublicKeyAddress());
            boolean verified = ECDSA.verifyECDSASignature(publicKey, transactionOutputsHash.getBytes(UTF_8), Hex.decode(transactionRequest.getSignature()));
            if (!verified){
                return false;
            }
        } catch (GeneralSecurityException e){
            return false;
        }

        if (!skipEqualityCheck) { //genesis transactions
            boolean hasBalance = hasBalance(transactionRequest, id);
            if (!hasBalance) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasBalance(AccountTransactionRequest transactionRequest, String id) {
        long sum = 0L;
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            sum += transactionOutput.getValue();
        }

        Long balance = Data.getAccountBalanceCache(id).get(transactionRequest.getPublicKeyAddress());
        return balance >= sum;
    }

}

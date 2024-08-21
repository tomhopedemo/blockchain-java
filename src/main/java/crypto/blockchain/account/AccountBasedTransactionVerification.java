package crypto.blockchain.account;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionVerification {

    AccountBalanceCache accountBalanceCache;

    public AccountBasedTransactionVerification(AccountBalanceCache accountBalanceCache) {
        this.accountBalanceCache = accountBalanceCache;
    }

    /**
     * skipEqualityCheck used for genesis transactions
     */
    public boolean verifySignature(AccountBasedTransactionRequest transactionRequest, boolean skipEqualityCheck) {
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

        if (!skipEqualityCheck) {
            boolean hasBalance = hasBalance(transactionRequest);
            if (!hasBalance) {
                return false;
            }
        }
        return true;
    }

    private boolean hasBalance(AccountBasedTransactionRequest transactionRequest) {
        long sum = 0L;
        for (AccountBasedTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            sum += transactionOutput.getValue();
        }

        Long balance = accountBalanceCache.get(transactionRequest.getPublicKeyAddress());
        return balance >= sum;
    }

}

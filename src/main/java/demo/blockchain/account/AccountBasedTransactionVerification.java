package demo.blockchain.account;

import demo.blockchain.TransactionInput;
import demo.blockchain.TransactionOutput;
import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionVerification {

    AccountBalanceCache accountBalanceCache;

    public AccountBasedTransactionVerification(AccountBalanceCache accountBalanceCache) {
        this.accountBalanceCache = accountBalanceCache;
    }

    public boolean verifySignature(AccountBasedTransactionRequest transactionRequest, boolean skipEqualityCheckForGenesisTransactions) {
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

        if (!skipEqualityCheckForGenesisTransactions) {
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

package crypto.blockchain.account;

import crypto.blockchain.Blockchain;
import crypto.blockchain.api.BlockchainData;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionVerification {

    /**
     * skipEqualityCheck used for genesis transactions
     */
    public static boolean verifySignature(AccountTransactionRequest transactionRequest, boolean skipEqualityCheck, Blockchain blockchain) {
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
            boolean hasBalance = hasBalance(transactionRequest, blockchain);
            if (!hasBalance) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasBalance(AccountTransactionRequest transactionRequest, Blockchain blockchain) {
        long sum = 0L;
        for (AccountTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            sum += transactionOutput.getValue();
        }

        Long balance = BlockchainData.getAccountBalanceCache(blockchain.getId()).get(transactionRequest.getPublicKeyAddress());
        return balance >= sum;
    }

}

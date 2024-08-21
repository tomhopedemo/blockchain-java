package demo.blockchain.utxo;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionVerification {

    TransactionCache transactionCache;

    public TransactionVerification(TransactionCache transactionCache) {
        this.transactionCache = transactionCache;
    }

    public boolean verifySignature(TransactionRequest transactionRequest, boolean skipEqualityCheckForGenesisTransactions) {
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.getTransactionOutputHash();
            TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
            if (transactionOutput == null){
                return false;
            }
            try {
                PublicKey publicKey = Encoder.decodeToPublicKey(transactionOutput.getRecipient());
                boolean verified = ECDSA.verifyECDSASignature(publicKey, transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.getSignature()));
                if (!verified){
                    return false;
                }
            } catch (GeneralSecurityException e){
                return false;
            }
        }

        if (!skipEqualityCheckForGenesisTransactions) {
            boolean inputSumEqualToOutputSum = isInputSumEqualToOutputSum(transactionRequest);
            if (!inputSumEqualToOutputSum) {
                return false;
            }
        }

        return true;
    }

    private boolean isInputSumEqualToOutputSum(TransactionRequest transactionRequest) {
        long sum = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = transactionCache.get(transactionInput.getTransactionOutputHash());
            sum += transactionOutput.getValue();
        }
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            sum -= transactionOutput.getValue();
        }
        return sum == 0L;
    }
}

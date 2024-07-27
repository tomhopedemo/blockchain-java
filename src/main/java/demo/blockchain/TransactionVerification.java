package demo.blockchain;

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

    public boolean verify(TransactionRequest transactionRequest, boolean skipEqualityCheckForGenesisTransactions) {
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.getTransactionOutputHash();
            TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
            boolean verified;
            try {
                PublicKey publicKey = Encoder.decodeToPublicKey(transactionOutput.recipient);
                verified = ECDSA.verifyECDSASignature(publicKey, transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.getSignature()));
            } catch (GeneralSecurityException e){
                return false;
            }
            if (!verified){
                return false;
            }
        }

        if (!skipEqualityCheckForGenesisTransactions) {
            boolean check = checkInputSumEqualToOutputSum(transactionRequest);
            if (!check) {
                return false;
            }
        }

        return true;
    }

    private boolean checkInputSumEqualToOutputSum(TransactionRequest transactionRequest) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = transactionCache.get(transactionInput.getTransactionOutputHash());
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfInputs += transactionOutputValue;
        }

        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfOutputs += transactionOutputValue;
        }
        return sumOfInputs == sumOfOutputs;
    }
}

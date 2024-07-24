package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionVerification {

    TransactionCache transactionCache;

    public TransactionVerification(TransactionCache transactionCache) {
        this.transactionCache = transactionCache;
    }

    public boolean verify(TransactionRequest transactionRequest, boolean skipEqualityCheck) throws Exception {
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.getTransactionOutputHash();
            TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
            boolean verified = ECDSA.verifyECDSASignature(Encoder.decodeToPublicKey(transactionOutput.recipient), transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.getSignature()));
            if (!verified){
                return false;
            }
        }

        if (!skipEqualityCheck) {
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
        System.out.println(sumOfInputs + "    " + sumOfOutputs);
        return sumOfInputs == sumOfOutputs;
    }
}

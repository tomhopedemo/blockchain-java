package crypto.blockchain.utxo;

import crypto.blockchain.Data;
import crypto.blockchain.TransactionOutput;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UTXOVerification {

    public static boolean verifySignature(UTXORequest request, String id) {
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.transactionOutputHash();
            TransactionOutput transactionOutput = Data.getUtxo(id, transactionOutputHash);
            if (transactionOutput == null){
                return false;
            }
            try {
                PublicKey publicKey = Encoder.decodeToPublicKey(transactionOutput.getRecipient());
                boolean verified = ECDSA.verifyECDSASignature(publicKey, transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.signature()));
                if (!verified){
                    return false;
                }
            } catch (GeneralSecurityException e){
                return false;
            }
        }
        boolean inputSumEqualToOutputSum = isInputSumEqualToOutputSum(request, id);
        if (!inputSumEqualToOutputSum) {
            return false;
        }
        return true;
    }

    private static boolean isInputSumEqualToOutputSum(UTXORequest transactionRequest, String id) {
        long sum = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = Data.getUtxo(id, transactionInput.transactionOutputHash());
            sum += transactionOutput.getValue();
        }
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            sum -= transactionOutput.getValue();
        }
        return sum == 0L;
    }
}

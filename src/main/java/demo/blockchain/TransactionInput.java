package demo.blockchain;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionInput {

    String transactionOutputHash;
    byte[] signature;

    public TransactionInput(String transactionOutputId, byte[] signature) {
        this.transactionOutputHash = transactionOutputId;
        this.signature = signature;
    }

    public String getTransactionOutputHash() {
        return transactionOutputHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public record TransactionInputData (
            String transactionOutputId,
            String signatureEncoded
    ){}

    public String serialise(){
        TransactionInputData transactionInputData = new TransactionInputData(this.transactionOutputHash, new String(this.signature, UTF_8));
        return transactionInputData.transactionOutputId() + transactionInputData.signatureEncoded();
    }

    //deserialize.

}

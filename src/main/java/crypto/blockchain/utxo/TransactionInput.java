package crypto.blockchain.utxo;

import crypto.encoding.Encoder;

public class TransactionInput {

    String transactionOutputHash;
    String signature;

    public TransactionInput(String transactionOutputId, byte[] signature) {
        this.transactionOutputHash = transactionOutputId;
        this.signature = Encoder.encodeToHexadecimal(signature);
    }

    public String getTransactionOutputHash() {
        return transactionOutputHash;
    }

    public String getSignature() {
        return signature;
    }

    public String serialise(){
        return this.transactionOutputHash + this.signature;
    }

}

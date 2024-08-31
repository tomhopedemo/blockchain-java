package crypto.blockchain.utxo;

import crypto.encoding.Encoder;

public record TransactionInput (String transactionOutputHash, String signature) {

    public TransactionInput(String transactionOutputHash, byte[] signature) {
        this(transactionOutputHash, Encoder.encodeToHexadecimal(signature));
    }

    public String serialise(){
        return this.transactionOutputHash + this.signature;
    }

}

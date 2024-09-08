package crypto.block.utxo;

import crypto.encoding.Encoder;

public record TransactionInput (String transactionOutputHash, String signature) {

    public TransactionInput(String transactionOutputHash, byte[] signature) {
        this(transactionOutputHash, Encoder.encodeToHexadecimal(signature));
    }

    public String serialise(){
        return transactionOutputHash + signature;
    }

}

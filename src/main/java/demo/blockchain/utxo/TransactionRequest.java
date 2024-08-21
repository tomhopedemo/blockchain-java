package demo.blockchain.utxo;

import demo.blockchain.BlockDataHashable;
import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.util.List;

public class TransactionRequest implements BlockDataHashable {

    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;
    public String transactionRequestHashHex;

    public TransactionRequest(List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) {
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
        this.transactionRequestHashHex = Encoder.encodeToHexadecimal(calculateTransactionHash());
    }

    private byte[] calculateTransactionHash() {
        String preHash = String.join("", getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()) +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        return Hashing.hash(preHash);
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

    public String getTransactionRequestHash() {
        return this.transactionRequestHashHex;
    }

    @Override
    public String blockDataHash() {
        return transactionRequestHashHex;
    }




}

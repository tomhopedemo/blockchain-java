package crypto.blockchain.utxo;

import crypto.blockchain.BlockDataHashable;
import crypto.blockchain.Request;
import crypto.blockchain.TransactionOutput;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class UTXORequest implements BlockDataHashable, Request {

    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;
    public String transactionRequestHash;

    @Override
    public String getBlockDataHash() {
        return transactionRequestHash;
    }

    private byte[] calculateTransactionHash() {
        String preHash = String.join("", getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()) +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        return Hashing.hash(preHash);
    }

    public UTXORequest(List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) {
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
        this.transactionRequestHash = Encoder.encodeToHexadecimal(calculateTransactionHash());
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

    public String getTransactionRequestHash() {
        return this.transactionRequestHash;
    }


}

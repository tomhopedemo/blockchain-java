package crypto.block.utxo;

import crypto.blockchain.Request;
import crypto.blockchain.TransactionOutput;

import java.util.List;

public class UTXORequest implements Request {

    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;

    @Override
    public String getPreHash(){
        return String.join("", getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()) +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
    }

    public UTXORequest(List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) {
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

}

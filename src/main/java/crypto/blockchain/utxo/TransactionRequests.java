package crypto.blockchain.utxo;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class TransactionRequests implements BlockDataHashable {

    List<TransactionRequest> transactionRequests;

    public TransactionRequests(List<TransactionRequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    public List<TransactionRequest> getTransactionRequests() {
        return transactionRequests;
    }

    @Override
    public String blockDataHash()  {
        String preHash = String.join("", transactionRequests.stream().map(transactionRequest -> transactionRequest.getTransactionRequestHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }
}

package crypto.blockchain.utxo;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class UTXORequests implements BlockDataHashable {

    List<UTXORequest> transactionRequests;

    public UTXORequests(List<UTXORequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    public List<UTXORequest> getTransactionRequests() {
        return transactionRequests;
    }

    @Override
    public String getBlockDataHash()  {
        String preHash = String.join("", transactionRequests.stream().map(transactionRequest -> transactionRequest.getTransactionRequestHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }
}

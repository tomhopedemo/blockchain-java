package demo.blockchain;

import java.util.HashMap;
import java.util.Map;

public class TransactionCache {

    public HashMap<String, TransactionOutput> unspentOutputsById = new HashMap<>();

    public TransactionCache() {
    }

    public TransactionOutput get(String transactionId) {
        return unspentOutputsById.get(transactionId);
    }

    public void put(String id, TransactionOutput transactionOutput) {
        unspentOutputsById.put(id, transactionOutput);
    }

    public void remove(String transactionId) {
        unspentOutputsById.remove(transactionId);
    }

    public Iterable<? extends Map.Entry<String, TransactionOutput>> entrySet() {
        return unspentOutputsById.entrySet();
    }
}

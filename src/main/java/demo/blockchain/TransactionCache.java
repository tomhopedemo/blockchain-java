package demo.blockchain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransactionCache {

    public HashMap<String, TransactionOutput> unspentOutputsById = new HashMap<>();

    public TransactionCache() {
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

    public Collection<TransactionOutput> values(){
        return unspentOutputsById.values();
    }
}

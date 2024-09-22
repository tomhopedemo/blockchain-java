package crypto.caches;


import crypto.TransactionOutput;

import java.util.HashMap;
import java.util.Map;

public class UTXOCache {

    public HashMap<String, TransactionOutput> unspentOutputsById = new HashMap<>();

    public void put(String transactionOutputHash, TransactionOutput transactionOutput) {
        unspentOutputsById.put(transactionOutputHash, transactionOutput);
    }

    public void remove(String transactionId) {
        unspentOutputsById.remove(transactionId);
    }

    public Iterable<? extends Map.Entry<String, TransactionOutput>> entrySet() {
        return unspentOutputsById.entrySet();
    }

    public TransactionOutput get(String transactionOutputHash){
        return unspentOutputsById.get(transactionOutputHash);
    }

    public boolean contains(String transactionOutputHash){
        return unspentOutputsById.containsKey(transactionOutputHash);
    }
}

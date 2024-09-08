package crypto;

import crypto.block.utxo.UTXOCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyUtxoCache {

    private final ConcurrentHashMap<String, UTXOCache> currencyUtxoCache = new ConcurrentHashMap<>();

    public void put(String currency, String transactionOutputHash, TransactionOutput transactionOutput){
        currencyUtxoCache.computeIfAbsent(currency, _ -> new UTXOCache()).put(transactionOutputHash, transactionOutput);
    }

    public void remove(String currency, String transactionId) {
        currencyUtxoCache.get(currency).remove(transactionId);
    }

    public TransactionOutput get(String currency, String transactionOutputHash){
        return currencyUtxoCache.get(currency).get(transactionOutputHash);
    }

    public UTXOCache get(String currency){
        return currencyUtxoCache.get(currency);
    }

    public Iterable<? extends Map.Entry<String, TransactionOutput>> entrySet(String currency) {
        return currencyUtxoCache.get(currency).entrySet();
    }

    public boolean contains(String currency, String transactionOutputHash){
        return currencyUtxoCache.get(currency).contains(transactionOutputHash);
    }

    public boolean hasCurrency(String currency) {
        return currencyUtxoCache.get(currency) != null;
    }

    public TransactionOutput find(String transactionOutputHash) {
        for (UTXOCache value : currencyUtxoCache.values()) {
            TransactionOutput transactionOutput = value.get(transactionOutputHash);
            if (transactionOutput != null) return transactionOutput;
        }
        return null;
    }
}

package crypto.blockchain;

import crypto.blockchain.utxo.UTXOCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    static Map<String, Set<BlockType>> allowedBlocktypes;
    static Map<String, Blockchain> chains;
    static Map<String, CurrencyAccountCache> currencyAccountCaches;
    static Map<String, UTXOCache> utxoCaches;
    static Map<String, CurrencyCache> currencyCache;

    static Map<String, KeyPairCache> keyPairCaches; //the key pair from the currency cache can be used also.

    static {
        allowedBlocktypes = new ConcurrentHashMap<>();
        chains = new ConcurrentHashMap<>();
        currencyAccountCaches = new ConcurrentHashMap<>();
        utxoCaches = new ConcurrentHashMap<>();
        keyPairCaches = new ConcurrentHashMap<>();
        currencyCache = new ConcurrentHashMap<>();
    }


    public static void addChain(Blockchain blockchain) {
        chains.put(blockchain.id, blockchain);
    }

    public static Blockchain getChain(String id){
        return chains.get(id);
    }

    public static boolean hasChain(String id) {
        return chains.containsKey(id);
    }

    public static void addKeyPair(String id, KeyPair keyPair) {
        keyPairCaches.computeIfAbsent(id, _ ->  new KeyPairCache()).addKeyPair(keyPair);
    }

    public static void addUtxo(String id, String transactionOutputHash, TransactionOutput transactionOutput) {
        utxoCaches.computeIfAbsent(id, _ ->  new UTXOCache()).put(transactionOutputHash, transactionOutput);
    }

    public static UTXOCache getUTXOCache(String id){
        return utxoCaches.get(id);
    }

    public static boolean hasUtxo(String id, String utxoOutputHash) {
        return utxoCaches.get(id).contains(utxoOutputHash);
    }

    public static void removeUtxo(String id, String utxoOutputHash) {
        utxoCaches.get(id).remove(utxoOutputHash);
    }

    public static TransactionOutput getUtxo(String id, String transactionOutputHash) {
        return utxoCaches.get(id).get(transactionOutputHash);
    }

    public static void addAccountBalance(String id, String recipient, String currency, long value) {
        currencyAccountCaches.computeIfAbsent(id, _ -> new CurrencyAccountCache()).add(recipient, currency, value);
    }

    public static void addType(String id, BlockType type) {
        allowedBlocktypes.computeIfAbsent(id, _ -> new HashSet<>()).add(type);
    }

    public static Set<BlockType> getBlockTypes(String id){
        return allowedBlocktypes.get(id);
    }

    public static List<String> getKeys(String id) {
        return keyPairCaches.get(id).getKeyPairs().stream().map(w -> w.getPublicKeyAddress()).toList();
    }

    public static Optional<KeyPair> getKeyPair(String id, String from) {
        return keyPairCaches.get(id).getKeyPair(from);
    }

    public static Long getAccountBalance(String id, String currency, String publicKey){
        CurrencyAccountCache currencyAccountCache = Data.currencyAccountCaches.get(id);
        if (currencyAccountCache == null){
            return 0L;
        }
        return currencyAccountCache.getBalance(publicKey, currency);
    }

    public static CurrencyRequest getCurrency(String id, String currency) {
        CurrencyCache currencies = currencyCache.get(id);
        if (currencies == null){
            return null;
        }
        Optional<CurrencyRequest> currencyRequest = currencies.get(currency);
        if (currencyRequest.isPresent()){
            return currencyRequest.get();
        } else {
            return null;
        }

    }

    public static boolean hasCurrency(String id, String currency) {
        return getCurrency(id, currency) != null;
    }

    public static void addCurrency(String id, CurrencyRequest currency) {
        currencyCache.computeIfAbsent(id, _ -> new CurrencyCache()).add(currency);
    }

    public static boolean hasAccountCache(String id, String currency) {
        CurrencyAccountCache caches = currencyAccountCaches.get(id);
        if (caches == null){
            return false;
        }
        return caches.hasCurrency(currency);
    }
}

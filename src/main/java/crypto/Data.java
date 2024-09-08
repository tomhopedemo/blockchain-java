package crypto;

import crypto.block.currency.CurrencyRequest;
import crypto.block.utxo.UTXOCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    static Map<String, Blockchain> chains;
    static Map<String, Set<BlockType>> allowedBlocktypes;

    static Map<String, CurrencyAccountCache> currencyAccountCaches;
    static Map<String, CurrencyCache> currencyCache;
    static Map<String, KeypairCache> keypairCaches;
    static Map<String, CurrencyUtxoCache> currencyUtxoCaches;

    static {
        chains = new ConcurrentHashMap<>();
        allowedBlocktypes = new ConcurrentHashMap<>();
        currencyAccountCaches = new ConcurrentHashMap<>();
        currencyCache = new ConcurrentHashMap<>();
        keypairCaches = new ConcurrentHashMap<>();
        currencyUtxoCaches = new ConcurrentHashMap<>();
    }

    //chain
    public static void addChain(Blockchain blockchain) {
        chains.put(blockchain.id, blockchain);
    }

    public static Blockchain getChain(String id){
        return chains.get(id);
    }

    public static boolean hasChain(String id) {
        return chains.containsKey(id);
    }

    //type
    public static void addType(String id, BlockType type) {
        allowedBlocktypes.computeIfAbsent(id, _ -> new HashSet<>()).add(type);
    }

    public static Set<BlockType> getTypes(String id){
        return allowedBlocktypes.get(id);
    }

    //account
    public static void addAccount(String id, String recipient, String currency, long value) {
        currencyAccountCaches.computeIfAbsent(id, _ -> new CurrencyAccountCache()).add(recipient, currency, value);
    }

    public static Long getAccount(String id, String currency, String publicKey){
        CurrencyAccountCache currencyAccountCache = Data.currencyAccountCaches.get(id);
        if (currencyAccountCache == null){
            return 0L;
        }
        return currencyAccountCache.getBalance(publicKey, currency);
    }

    public static boolean hasAccountCache(String id, String currency) {
        CurrencyAccountCache caches = currencyAccountCaches.get(id);
        if (caches == null) return false;
        return caches.hasCurrency(currency);
    }

    //currency
    public static CurrencyRequest getCurrency(String id, String currency) {
        CurrencyCache currencies = currencyCache.get(id);
        if (currencies == null) return null;
        Optional<CurrencyRequest> currencyRequest = currencies.get(currency);
        return currencyRequest.isPresent() ? currencyRequest.get() : null;
    }

    public static boolean hasCurrency(String id, String currency) {
        return getCurrency(id, currency) != null;
    }

    public static void addCurrency(String id, CurrencyRequest currency) {
        currencyCache.computeIfAbsent(id, _ -> new CurrencyCache()).add(currency);
    }


    //keypair
    public static void addKeypair(String id, Keypair keypair) {
        keypairCaches.computeIfAbsent(id, _ ->  new KeypairCache()).addKeypair(keypair);
    }

    public static List<String> getKeys(String id) {
        return keypairCaches.get(id).getKeypairs().stream().map(w -> w.publicKey()).toList();
    }

    public static Keypair getKeypair(String id, String publicKey) {
        return keypairCaches.get(id).getKeypair(publicKey);
    }

    public static boolean hasKey(String id, String publicKey) {
        return getKeypair(id, publicKey) != null;
    }

    //UTXO
    public static void addUtxo(String id, String currency, String transactionOutputHash, TransactionOutput transactionOutput) {
        currencyUtxoCaches.computeIfAbsent(id, _ ->  new CurrencyUtxoCache()).put(currency, transactionOutputHash, transactionOutput);
    }

    public static void removeUtxo(String id, String currency, String utxoOutputHash) {
        currencyUtxoCaches.get(id).get(currency).remove(utxoOutputHash);
    }

    public static TransactionOutput getUtxo(String id, String transactionOutputHash) {
        return currencyUtxoCaches.get(id).find(transactionOutputHash);
    }

    public static boolean hasUtxo(String id, String utxoOutputHash) {
        return currencyUtxoCaches.get(id).find(utxoOutputHash) != null;
    }

    public static UTXOCache getUTXOCache(String id, String currency){
        return currencyUtxoCaches.get(id).get(currency);
    }

}

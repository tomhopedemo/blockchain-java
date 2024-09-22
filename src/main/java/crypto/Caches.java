package crypto;

import crypto.block.*;
import crypto.block.Currency;
import crypto.caches.UTXOCache;
import crypto.caches.*;
import crypto.hashing.Hashing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Caches {

    static Map<String, Blockchain> chains;
    static Map<String, Set<Class<? extends Request>>> allowedBlocktypes;

    static Map<String, String> createCache;
    static Map<String, Boolean> publishedCache;

    static Map<String, CurrencyAccountCache> currencyAccountCaches;
    static Map<String, BranchCache> branchCaches;
    static Map<String, CurrencyCache> currencyCaches;
    static Map<String, ReferendumCache> referendumCaches;
    static Map<String, KeypairCache> keypairCaches;
    static Map<String, CurrencyUtxoCache> currencyUtxoCaches;
    static Map<String, CurrencyStakeCache> currencyStakeCaches;
    static Map<String, DifficultyCache> difficultyCaches;
    static Map<String, Hashing.Type> protocolCache;

    static {
        chains = new ConcurrentHashMap<>();
        allowedBlocktypes = new ConcurrentHashMap<>();
        publishedCache = new ConcurrentHashMap<>();
        currencyAccountCaches = new ConcurrentHashMap<>();
        currencyCaches = new ConcurrentHashMap<>();
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
    public static void addType(String id, Class<? extends Request> type) {
        allowedBlocktypes.computeIfAbsent(id, _ -> new HashSet<>()).add(type);
    }

    public static Set<Class<? extends Request>> getTypes(String id){
        return allowedBlocktypes.get(id);
    }

    //account
    public static void addAccount(String id, String recipient, String currency, long value) {
        currencyAccountCaches.computeIfAbsent(id, _ -> new CurrencyAccountCache()).add(recipient, currency, value);
    }

    public static Long getAccount(String id, String currency, String publicKey){
        CurrencyAccountCache currencyAccountCache = Caches.currencyAccountCaches.get(id);
        if (currencyAccountCache == null) return 0L;
        return currencyAccountCache.getBalance(publicKey, currency);
    }

    public static boolean hasAccountCache(String id, String currency) {
        CurrencyAccountCache caches = currencyAccountCaches.get(id);
        if (caches == null) return false;
        return caches.hasCurrency(currency);
    }

    //branch

    public static void addBranch(String id, Branch branch) {
        branchCaches.computeIfAbsent(id, _ -> new BranchCache()).add(branch);
    }

    //currency
    public static void addCurrency(String id, Currency currency) {
        currencyCaches.computeIfAbsent(id, _ -> new CurrencyCache()).add(currency);
    }

    public static Currency getCurrency(String id, String currency) {
        CurrencyCache currencies = currencyCaches.get(id);
        if (currencies == null) return null;
        Optional<Currency> currencyRequest = currencies.get(currency);
        return currencyRequest.isPresent() ? currencyRequest.get() : null;
    }

    public static boolean hasCurrency(String id, String currency) {
        return getCurrency(id, currency) != null;
    }

    //Difficulty
    public static void addDifficulty(String id, Difficulty request) {
        difficultyCaches.computeIfAbsent(id, _ -> new DifficultyCache()).add(request);
    }

    //keypair
    public static void addKeypair(String id, Keypair keypair) {
        keypairCaches.computeIfAbsent(id, _ ->  new KeypairCache()).addKeypair(keypair);
    }

    public static Keypair getKeypair(String id, String publicKey) {
        KeypairCache cache = keypairCaches.get(id);
        if (cache == null) return null;
        return cache.getKeypair(publicKey);
    }

    public static List<String> getKeys(String id) {
        return keypairCaches.get(id).getKeypairs().stream().map(w -> w.publicKey()).toList();
    }

    public static boolean hasKey(String id, String publicKey) {
        return getKeypair(id, publicKey) != null;
    }

    //StakeCacheItem
    public static void addStake(String id, String currency, String publicKey, long value, int expiry) {
        currencyStakeCaches.computeIfAbsent(id, _ -> new CurrencyStakeCache()).add(publicKey, currency, value, expiry);
    }

    //UTXO
    public static void addUtxo(String id, String currency, String utxoHash, TransactionOutput transactionOutput) {
        currencyUtxoCaches.computeIfAbsent(id, _ ->  new CurrencyUtxoCache()).put(currency, utxoHash, transactionOutput);
    }

    public static void removeUtxo(String id, String currency, String utxoHash) {
        currencyUtxoCaches.get(id).get(currency).remove(utxoHash);
    }

    public static TransactionOutput getUtxo(String id, String utxoHash) {
        return currencyUtxoCaches.get(id).find(utxoHash);
    }

    public static boolean hasUtxo(String id, String utxoHash) {
        return currencyUtxoCaches.get(id).find(utxoHash) != null;
    }

    public static UTXOCache getUTXOCache(String id, String currency){
        return currencyUtxoCaches.get(id).get(currency);
    }

    public static boolean isPublished(String id) {
        return publishedCache.get(id);
    }

    public static void create(String id, String key) {
        createCache.put(id, key);
    }

    public static void publish(String id) {
        publishedCache.put(id, true);
    }

    public static void setHashType(String id, Hashing.Type hashType) {
        protocolCache.put(id, hashType);
    }

    public static Hashing.Type getHashType(String id) {
        Hashing.Type type = protocolCache.get(id);
        return type == null ? Hashing.Type.SHA3_256 : type;
    }

    public static void removeBranch(String id, String branchKey) {
        BranchCache branchCache = branchCaches.get(id);
        if (branchCache == null) return;
        branchCache.remove(branchKey);
    }

    public static void addReferendum(String id, Referendum request) {
        referendumCaches.computeIfAbsent(id, _ -> new ReferendumCache()).add(request);
    }
}

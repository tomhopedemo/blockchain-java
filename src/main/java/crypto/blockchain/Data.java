package crypto.blockchain;

import crypto.blockchain.account.AccountCache;
import crypto.blockchain.account.AccountCaches;
import crypto.blockchain.utxo.UTXOCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    static Map<String, Set<BlockType>> allowedBlocktypes;
    static Map<String, Blockchain> blockchains;
    static Map<String, AccountCaches> accountCaches;
    static Map<String, UTXOCache> utxoCaches;

    static Map<String, KeyPairCache> keyPairCaches;

    static {
        allowedBlocktypes = new ConcurrentHashMap<>();
        blockchains = new ConcurrentHashMap<>();
        accountCaches = new ConcurrentHashMap<>();
        utxoCaches = new ConcurrentHashMap<>();
        keyPairCaches = new ConcurrentHashMap<>();
    }


    public static void addChain(Blockchain blockchain) {
        blockchains.put(blockchain.id, blockchain);
    }

    public static Blockchain getChain(String id){
        return blockchains.get(id);
    }

    public static boolean hasChain(String id) {
        return blockchains.containsKey(id);
    }

    public static void addKeyPair(String id, KeyPair keyPair) {
        keyPairCaches.computeIfAbsent(id, _ ->  new KeyPairCache()).addKeyPair(keyPair);
    }

    public static void addUtxo(String id, String transactionOutputHash, TransactionOutput genesisTransactionOutput) {
        utxoCaches.computeIfAbsent(id, _ ->  new UTXOCache()).put(transactionOutputHash, genesisTransactionOutput);
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
        accountCaches.computeIfAbsent(id, _ -> new AccountCaches()).add(recipient, currency, value);
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
        AccountCaches accountCaches = Data.accountCaches.get(id);
        if (accountCaches == null){
            return 0L;
        }
        return accountCaches.get(publicKey, currency);
    }

    //note that the currency can only be used by account chains for now
    public static boolean hasCurrency(String id, String currency) {
        AccountCaches accountCaches = Data.accountCaches.get(id);
        if (accountCaches == null){
            return false;
        }
        return accountCaches.hasCurrency(currency);
    }
}

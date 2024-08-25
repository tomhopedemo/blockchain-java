package crypto.blockchain;

import crypto.blockchain.account.AccountCache;
import crypto.blockchain.utxo.UTXOCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    static Map<String, Set<BlockType>> allowedBlocktypes;
    static Map<String, Blockchain> blockchains;
    static Map<String, AccountCache> accountCaches;
    static Map<String, UTXOCache> utxoCaches;

    static Map<String, WalletCache> walletCaches;

    static {
        blockchains = new ConcurrentHashMap<>();
        accountCaches = new ConcurrentHashMap<>();
        utxoCaches = new ConcurrentHashMap<>();
        walletCaches = new ConcurrentHashMap<>();
    }


    public static void addChain(Blockchain blockchain) {
        blockchains.put(blockchain.id, blockchain);
    }

    public static Blockchain getChain(String id){
        return blockchains.get(id);
    }


    public static void addWallet(String id, Wallet wallet) {
        walletCaches.putIfAbsent(id, new WalletCache());
        walletCaches.get(id).addWallet(wallet);
    }

    public static void addUtxo(String id, String transactionOutputHash, TransactionOutput genesisTransactionOutput) {
        utxoCaches.putIfAbsent(id, new UTXOCache());
        utxoCaches.get(id).put(transactionOutputHash, genesisTransactionOutput);
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

    public static void addAccountBalance(String id, String recipient, long value) {
        accountCaches.putIfAbsent(id, new AccountCache());
        accountCaches.get(id).add(recipient, value);
    }

    public static void subtractAccountBalance(String id, String from, long value) {
        accountCaches.putIfAbsent(id, new AccountCache());
        accountCaches.get(id).subtract(from, value);
    }



    public static void addType(String id, BlockType type) {
        allowedBlocktypes.putIfAbsent(id, new HashSet<>());
        allowedBlocktypes.get(id).add(type);
    }





    public static AccountCache getAccountBalanceCache(String id){
        return accountCaches.get(id);
    }

    public static Wallet getGenesisWallet(String id) {
        return walletCaches.get(id).getGenesisWallet();
    }

    public static List<String> getKeys(String id) {
        return walletCaches.get(id).getWallets().stream().map(w -> w.publicKeyAddress).toList();
    }

    public static Optional<Wallet> getWallet(String id, String from) {
        return walletCaches.get(id).getWallet(from);
    }


}

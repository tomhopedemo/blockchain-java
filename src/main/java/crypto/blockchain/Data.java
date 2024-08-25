package crypto.blockchain;

import crypto.blockchain.account.AccountBalanceCache;
import crypto.blockchain.utxo.TransactionCache;
import crypto.blockchain.utxo.TransactionOutput;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    static Map<String, Set<BlockType>> allowedBlocktypes;
    static Map<String, Blockchain> blockchains;
    static Map<String, AccountBalanceCache> accountBalanceCaches;
    static Map<String, TransactionCache> transactionCaches;

    static Map<String, WalletCache> walletCaches;

    static {
        blockchains = new ConcurrentHashMap<>();
        accountBalanceCaches = new ConcurrentHashMap<>();
        transactionCaches = new ConcurrentHashMap<>();
        walletCaches = new ConcurrentHashMap<>();
    }


    public static void addChain(Blockchain blockchain) {
        blockchains.put(blockchain.id, blockchain);
    }

    public static void addWallet(String id, Wallet wallet) {
        walletCaches.putIfAbsent(id, new WalletCache());
        walletCaches.get(id).addWallet(wallet);
    }

    public static void addTransaction(String id, String transactionOutputHash, TransactionOutput genesisTransactionOutput) {
        transactionCaches.putIfAbsent(id, new TransactionCache());
        transactionCaches.get(id).put(transactionOutputHash, genesisTransactionOutput);
    }

    public static void addAccountTransaction(String id, String transactionOutputHash, TransactionOutput genesisTransactionOutput) {
        transactionCaches.putIfAbsent(id, new TransactionCache());
        transactionCaches.get(id).put(transactionOutputHash, genesisTransactionOutput);
    }

    public static void addAccountBalance(String id, String recipient, long value) {
        accountBalanceCaches.putIfAbsent(id, new AccountBalanceCache());
        accountBalanceCaches.get(id).add(recipient, value);
    }

    public static void subtractAccountBalance(String id, String from, long value) {
        accountBalanceCaches.putIfAbsent(id, new AccountBalanceCache());
        accountBalanceCaches.get(id).subtract(from, value);
    }

    public static void addType(String id, BlockType type) {
        allowedBlocktypes.putIfAbsent(id, new HashSet<>());
        allowedBlocktypes.get(id).add(type);
    }




    public static Blockchain getBlockchain(String id){
        return blockchains.get(id);
    }

    public static TransactionCache getTransactionCache(String id){
        return transactionCaches.get(id);
    }

    public static AccountBalanceCache getAccountBalanceCache(String id){
        return accountBalanceCaches.get(id);
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

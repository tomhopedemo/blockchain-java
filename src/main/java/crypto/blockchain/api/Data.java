package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.Wallet;
import crypto.blockchain.WalletCache;
import crypto.blockchain.account.AccountBalanceCache;
import crypto.blockchain.utxo.TransactionCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

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


    public static void addBlockchain(Blockchain blockchain) {
        blockchains.put(blockchain.id, blockchain);
    }

    public static void addGenesisWallet(String id, Wallet genesis) {
        walletCaches.get(id).addGenesisWallet(genesis);
    }

    public static void addWalletCache(String id){
        walletCaches.put(id, new WalletCache());
    }

    public static void addWallet(String id, Wallet wallet) {
        walletCaches.get(id).addWallet(wallet);
    }

    public static void addTransactionCache(String id){
        transactionCaches.put(id, new TransactionCache());
    }

    public static void addAccountBalanceCache(String id){
        accountBalanceCaches.put(id, new AccountBalanceCache());
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

}

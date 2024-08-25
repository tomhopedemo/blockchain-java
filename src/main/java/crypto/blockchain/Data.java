package crypto.blockchain;

import crypto.blockchain.account.AccountBalanceCache;
import crypto.blockchain.utxo.TransactionCache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    public static List<String> getKeys(String id) {
        return walletCaches.get(id).getWallets().stream().map(w -> w.publicKeyAddress).toList();
    }

    public static Optional<Wallet> getWallet(String id, String from) {
        return walletCaches.get(id).getWallet(from);
    }

    public static void addType(String id, BlockType type) {
        allowedBlocktypes.get(id).add(type);
    }
}

package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;

public class AccountBasedBlockchainTech {

    public static Blockchain execute(String id, int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain(id);
        Wallet genesis = Wallet.generate();

        //need to maintain the account balance cache etc.
        AccountBalanceCache accountBalanceCache = new AccountBalanceCache();

        AccountBasedTransactionBlockMining transactionBlockMining = new AccountBasedTransactionBlockMining(blockchain, difficulty, accountBalanceCache);
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(accountBalanceCache);

        //Mining
        AccountBasedTransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        BlockchainData.addAccountBalanceCache(id, accountBalanceCache);
        BlockchainData.addGenesisWallet(id, genesis);
        return blockchain;
    }

    public static Blockchain simulate(Blockchain blockchain, AccountBalanceCache accountBalanceCache, int numBlocks, int difficulty) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(accountBalanceCache);
        AccountBasedTransactionBlockMining transactionBlockMining = new AccountBasedTransactionBlockMining(blockchain, difficulty, accountBalanceCache);
        for (int i = 0; i < numBlocks; i++) {
            AccountBasedTransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(genesis, wallet.publicKeyAddress, 5).get();
            transactionBlockMining.mineNextBlock(transactionRequest);
        }
        BlockchainData.addWallet(blockchain.getId(), wallet);
        return blockchain;
    }
}

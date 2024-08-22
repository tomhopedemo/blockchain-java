package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;

public class TransactionalBlockchain {

    public static void genesis(Blockchain blockchain, int difficulty, long genesisTransactionValue, Wallet genesis) {
        TransactionCache transactionCache = new TransactionCache();
        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        BlockchainData.addTransactionCache(blockchain.getId(), transactionCache);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
    }

    public static void simulate(Blockchain blockchain, TransactionCache transactionCache, int numBlocks, int difficulty) {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        TransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(genesis, wallet.publicKeyAddress, 5).get();
        transactionBlockMining.mineNextBlock(transactionRequest);

        BlockchainData.addWallet(blockchain.getId(), wallet);
    }
}

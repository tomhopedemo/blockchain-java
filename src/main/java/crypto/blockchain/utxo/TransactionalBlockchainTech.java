package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainService;

public class TransactionalBlockchainTech {

    public static Blockchain execute(String id, int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain(id);
        Wallet genesis = Wallet.generate();
        Wallet walletB = Wallet.generate();

        TransactionCache transactionCache = new TransactionCache();
        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        //Mining
        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        BlockchainData.addTransactionCache(id, transactionCache);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
        return blockchain;
    }

    public static Blockchain simulate(Blockchain blockchain, TransactionCache transactionCache, int numBlocks, int difficulty) {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        TransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(genesis, wallet.publicKeyAddress, 5).get();
        transactionBlockMining.mineNextBlock(transactionRequest);

        BlockchainData.addWallet(blockchain.getId(), wallet);
        return blockchain;
    }
}

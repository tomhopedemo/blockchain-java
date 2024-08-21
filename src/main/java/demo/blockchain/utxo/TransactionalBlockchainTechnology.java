package demo.blockchain.utxo;

import demo.blockchain.*;

public class TransactionalBlockchainTechnology {

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(2).generate();
        Wallet walletA = walletStore.get(0);
        Wallet walletB = walletStore.get(1);

        TransactionCache transactionCache = new TransactionCache();
        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(walletStore, transactionCache);

        //Mining
        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletA, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        TransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5).get();
        transactionBlockMining.mineNextBlock(transactionRequest);

        //Validation
        BlockchainValidator blockchainValidator = new BlockchainValidator();
        blockchainValidator.validate(blockchain);

        //Serialisation
        new BlockchainSerialisation().checkSerializationStable(blockchain);

        //Visualization
        if (Control.VISUALIZE_IN_CONSOLE) {
            new Visualiser().visualise(blockchain, transactionCache, walletStore);
        }

        System.out.println("Complete.");
    }
}

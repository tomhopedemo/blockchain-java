package demo.blockchain.account;

import demo.blockchain.*;

public class AccountBasedBlockchainTechnology {

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(2).generate();
        Wallet walletA = walletStore.get(0);
        Wallet walletB = walletStore.get(1);

        AccountBalanceCache accountBalanceCache = new AccountBalanceCache();
        AccountBasedTransactionBlockMining transactionBlockMining = new AccountBasedTransactionBlockMining(blockchain, difficulty, accountBalanceCache);
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(walletStore, accountBalanceCache);

        //Mining
        AccountBasedTransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletA, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        AccountBasedTransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5).get();
        transactionBlockMining.mineNextBlock(transactionRequest);

        //Validation
        BlockchainStore blockchainStore = new BlockchainStore();
        blockchainStore.add(blockchain);
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Serialisation
        new BlockchainSerialisation().checkSerializationStable(blockchain);

        //Visualization
        if (Control.VISUALIZE_IN_CONSOLE) {
            new Visualiser().visualise(blockchain, accountBalanceCache, walletStore);
        }

        System.out.println("Complete.");
    }


}

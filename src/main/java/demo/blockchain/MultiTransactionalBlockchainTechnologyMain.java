package demo.blockchain;

import java.security.Security;
import java.util.List;

public class MultiTransactionalBlockchainTechnologyMain {


    public static void main(String[] args) throws Exception {
        int difficulty = 4;
        long genesisTransactionValue = 100;

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(3).generate();
        Wallet walletA = walletStore.get(0);
        Wallet walletB = walletStore.get(1);
        Wallet walletC = walletStore.get(2);

        TransactionCache transactionCache = new TransactionCache();
        MultiTransactionBlockMining transactionBlockMining = new MultiTransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(walletStore, transactionCache);

        //Mining
        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletA, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(new TransactionRequests(List.of(genesisTransactionRequest)));


        //because you can transac multi to multi, it may be okay to have the block miner just select one
        //and push the other one.
        TransactionRequest transactionRequest1 = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5);
        TransactionRequest transactionRequest2 = transactionRequestFactory.createTransactionRequest(walletA, walletC.publicKeyAddress, 7);
        transactionBlockMining.mineNextBlock(new TransactionRequests(List.of(transactionRequest1, transactionRequest2)));

        //Validation
        BlockchainStore blockchainStore = new BlockchainStore();
        blockchainStore.add(blockchain);
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Serialisation
        BlockchainSerialisation blockchainSerialisation = new BlockchainSerialisation();
        boolean stable = blockchainSerialisation.checkSerializationStable(blockchain);
        System.out.println("serialization stable:" + stable);

        //Visualization
        Visualiser visualiser = new Visualiser();
        visualiser.visualise(blockchain);
        visualiser.visualise(transactionCache);
        visualiser.visualise(walletStore);
    }
}

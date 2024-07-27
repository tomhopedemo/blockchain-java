package demo.blockchain;

import java.security.Security;
import java.util.List;

import static demo.blockchain.Control.VISUALIZE_IN_CONSOLE;

public class MultiTransactionalBlockchainTechnology {

    public MultiTransactionalBlockchainTechnology() {
    }

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {

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

        TransactionRequest transactionRequest1 = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5).get();
        TransactionRequest transactionRequest2 = transactionRequestFactory.createTransactionRequest(walletA, walletC.publicKeyAddress, 7).get();
        List<TransactionRequest> transactionRequests = List.of(transactionRequest1, transactionRequest2);

        TransactionRequests transactionRequestsForNextBlock = transactionBlockMining.constructTransactionRequestsForNextBlock(transactionRequests);
        transactionBlockMining.mineNextBlock(transactionRequestsForNextBlock);

        //Validation
        BlockchainStore blockchainStore = new BlockchainStore();
        blockchainStore.add(blockchain);
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Serialisation
        BlockchainSerialisation blockchainSerialisation = new BlockchainSerialisation();

        boolean stable = blockchainSerialisation.checkSerializationStable(blockchain);
        if (!stable){
            throw new BlockchainException("Unstable Blockchain Serialization");
        }
        //Visualization
        if (VISUALIZE_IN_CONSOLE) {
            Visualiser visualiser = new Visualiser();
            visualiser.visualise(blockchain);
            visualiser.visualise(transactionCache);
            visualiser.visualise(walletStore);
        }
        System.out.println("Complete.");
    }
}

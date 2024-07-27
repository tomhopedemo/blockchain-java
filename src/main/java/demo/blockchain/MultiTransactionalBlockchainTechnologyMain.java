package demo.blockchain;

import java.security.Security;
import java.util.List;

public class MultiTransactionalBlockchainTechnologyMain {

    //1. additional transactionality input checking - how do we ensure that the input hasn't been
    //used anywhere in the blockchain before.

    //2. output visualization switch

    //3. Test suite to ensure existing technologies run without Exceptions.

    //4. singular point of control for shared parameters

    final static boolean VISUALIZE_IN_CONSOLE = true;

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

        TransactionRequest transactionRequest1 = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5);
        TransactionRequest transactionRequest2 = transactionRequestFactory.createTransactionRequest(walletA, walletC.publicKeyAddress, 7);
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

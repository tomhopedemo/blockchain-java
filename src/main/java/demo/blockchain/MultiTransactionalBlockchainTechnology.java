package demo.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static demo.blockchain.Control.VISUALIZE_IN_CONSOLE;

public class MultiTransactionalBlockchainTechnology {

    public MultiTransactionalBlockchainTechnology() {
    }

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {
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


        //Example transaction stream and processing
        List<TransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            createAndRegisterSimpleTransactionRequest(transactionRequestFactory, walletA, walletB, transactionRequestsQueue, 5);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<TransactionRequests> transactionRequestsForNextBlock = transactionBlockMining.constructTransactionRequestsForNextBlock(transactionRequestsQueue);
            if (transactionRequestsForNextBlock.isPresent()) {
                transactionBlockMining.mineNextBlock(transactionRequestsForNextBlock.get());
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }

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

    private static void createAndRegisterSimpleTransactionRequest(TransactionRequestFactory transactionRequestFactory, Wallet walletA, Wallet walletB, List<TransactionRequest> transactionRequestsQueue, int value) {
        Optional<TransactionRequest> transactionRequestOptional = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value);
        if (transactionRequestOptional.isPresent()){
            TransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }
}

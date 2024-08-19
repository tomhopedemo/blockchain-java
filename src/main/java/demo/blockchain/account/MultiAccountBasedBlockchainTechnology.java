package demo.blockchain.account;

import demo.blockchain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static demo.blockchain.Control.VISUALIZE_IN_CONSOLE;

public class MultiAccountBasedBlockchainTechnology {

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(3).generate();
        Wallet walletA = walletStore.get(0);
        Wallet walletB = walletStore.get(1);

        AccountBalanceCache accountBalanceCache = new AccountBalanceCache();
        MultiAccountBasedBlockMining transactionBlockMining = new MultiAccountBasedBlockMining(blockchain, difficulty, accountBalanceCache);
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(walletStore, accountBalanceCache);

        //Mining
        AccountBasedTransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletA, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(new AccountBasedTransactionRequests(List.of(genesisTransactionRequest)));


        //Example transaction stream and processing
        List<AccountBasedTransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            createAndRegisterSimpleTransactionRequest(transactionRequestFactory, walletA, walletB, transactionRequestsQueue, 5);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<AccountBasedTransactionRequests> transactionRequestsForNextBlock = transactionBlockMining.constructTransactionRequestsForNextBlock(transactionRequestsQueue);
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
        new BlockchainSerialisation().checkSerializationStable(blockchain);

        //Visualization
        if (VISUALIZE_IN_CONSOLE) {
            Visualiser visualiser = new Visualiser();
            visualiser.visualise(blockchain, accountBalanceCache, walletStore);
        }

        System.out.println("Complete.");
    }

    private static void createAndRegisterSimpleTransactionRequest(AccountBasedTransactionRequestFactory transactionRequestFactory, Wallet walletA, Wallet walletB, List<AccountBasedTransactionRequest> transactionRequestsQueue, int value) throws BlockchainException {
        Optional<AccountBasedTransactionRequest> transactionRequestOptional = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value);
        if (transactionRequestOptional.isPresent()){
            AccountBasedTransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

}

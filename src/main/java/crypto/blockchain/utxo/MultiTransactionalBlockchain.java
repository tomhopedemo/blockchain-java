package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiTransactionalBlockchain {

    public static void genesis(Blockchain blockchain, int difficulty, long genesisTransactionValue, Wallet genesis) throws BlockchainException {
        TransactionCache transactionCache = new TransactionCache();
        MultiTransactionBlockMining transactionBlockMining = new MultiTransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(new TransactionRequests(List.of(genesisTransactionRequest)));
        BlockchainData.addTransactionCache(blockchain.getId(), transactionCache);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
    }

    private static void createAndRegisterSimpleTransactionRequest(TransactionRequestFactory transactionRequestFactory, Wallet walletA, Wallet walletB, List<TransactionRequest> transactionRequestsQueue, int value) {
        Optional<TransactionRequest> transactionRequestOptional = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value);
        if (transactionRequestOptional.isPresent()){
            TransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

    public static void simulate(Blockchain blockchain, TransactionCache transactionCache, int numBlocks, int difficulty) {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        MultiTransactionBlockMining transactionBlockMining = new MultiTransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(transactionCache);

        List<TransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            createAndRegisterSimpleTransactionRequest(transactionRequestFactory, genesis, wallet, transactionRequestsQueue, 5);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<TransactionRequests> transactionRequestsForNextBlock = transactionBlockMining.constructTransactionRequestsForNextBlock(transactionRequestsQueue);
            if (transactionRequestsForNextBlock.isPresent()) {
                transactionBlockMining.mineNextBlock(transactionRequestsForNextBlock.get());
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }
        BlockchainData.addWallet(blockchain.getId(), wallet);
    }
}

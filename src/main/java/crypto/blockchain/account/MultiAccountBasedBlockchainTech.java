package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainService;
import crypto.blockchain.utxo.TransactionCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiAccountBasedBlockchainTech {

    public static Blockchain execute(String id, int difficulty, long genesisTransactionValue) throws BlockchainException {
        //Construction
        Blockchain blockchain = new Blockchain(id);
        Wallet genesis = Wallet.generate();

        AccountBalanceCache accountBalanceCache = new AccountBalanceCache();
        MultiAccountBasedBlockMining transactionBlockMining = new MultiAccountBasedBlockMining(blockchain, difficulty, accountBalanceCache);
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(accountBalanceCache);

        //Mining
        AccountBasedTransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(new AccountBasedTransactionRequests(List.of(genesisTransactionRequest)));

        BlockchainData.addAccountBalanceCache(id, accountBalanceCache);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
        return blockchain;
    }

    private static void createAndRegisterSimpleTransactionRequest(AccountBasedTransactionRequestFactory transactionRequestFactory, Wallet walletA, Wallet walletB, List<AccountBasedTransactionRequest> transactionRequestsQueue, int value) throws BlockchainException {
        Optional<AccountBasedTransactionRequest> transactionRequestOptional = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value);
        if (transactionRequestOptional.isPresent()){
            AccountBasedTransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

    public static Blockchain simulate(Blockchain blockchain, AccountBalanceCache accountBalanceCache, int numBlocks, int difficulty) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        MultiAccountBasedBlockMining transactionBlockMining = new MultiAccountBasedBlockMining(blockchain, difficulty, accountBalanceCache);
        AccountBasedTransactionRequestFactory transactionRequestFactory = new AccountBasedTransactionRequestFactory(accountBalanceCache);

        //Example transaction stream and processing
        List<AccountBasedTransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            createAndRegisterSimpleTransactionRequest(transactionRequestFactory, genesis, wallet, transactionRequestsQueue, 5);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<AccountBasedTransactionRequests> transactionRequestsForNextBlock = transactionBlockMining.constructTransactionRequestsForNextBlock(transactionRequestsQueue);
            if (transactionRequestsForNextBlock.isPresent()) {
                transactionBlockMining.mineNextBlock(transactionRequestsForNextBlock.get());
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }

        BlockchainData.addWallet(blockchain.getId(), wallet);
        return blockchain;
    }
}

package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;

import java.util.*;

public class MultiAccountBasedBlockchain {

    public static void mineNextBlock(AccountBasedTransactionRequests transactionRequests, Blockchain blockchain, int difficulty, AccountBalanceCache accountBalanceCache) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis =  mostRecentBlock == null;

        //Individual Transaction Verification
        if (!isGenesis) {
            for (AccountBasedTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
                boolean verified = AccountBasedTransactionVerification.verifySignature(transactionRequest, false, blockchain);
                if (!verified) {
                    return;
                }
            }
        }

        //Overall Verification (no repeat accounts)
        Set<String> accounts = new HashSet<>();
        for (AccountBasedTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            if (accounts.contains(transactionRequest.getPublicKeyAddress())){
                return;
            }
            accounts.add(transactionRequest.getPublicKeyAddress());
        }


        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (AccountBasedTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (AccountBasedTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                accountBalanceCache.add(transactionOutput.getRecipient(), transactionOutput.getValue());
                if (!isGenesis) {
                    accountBalanceCache.subtract(transactionRequest.publicKeyAddress, transactionOutput.getValue());
                }
            }
        }
    }

    public static Optional<AccountBasedTransactionRequests> constructTransactionRequestsForNextBlock(List<AccountBasedTransactionRequest> availableTransactionRequests, Blockchain blockchain) {
        List<AccountBasedTransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (AccountBasedTransactionRequest transactionRequest : availableTransactionRequests) {
            //verify signature
            boolean verified = AccountBasedTransactionVerification.verifySignature(transactionRequest, false, blockchain);
            if (!verified){
                continue;
            }
            transactionRequestsToInclude.add(transactionRequest);
        }
        if (transactionRequestsToInclude.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new AccountBasedTransactionRequests(transactionRequestsToInclude));
        }
    }

    //register and genesis.
    public static void genesis(Blockchain blockchain, int difficulty, long genesisTransactionValue, Wallet genesis) throws BlockchainException {
        AccountBalanceCache accountBalanceCache = new AccountBalanceCache();
        AccountBasedTransactionRequest genesisTransactionRequest = AccountBasedTransactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        mineNextBlock(new AccountBasedTransactionRequests(List.of(genesisTransactionRequest)), blockchain, difficulty, accountBalanceCache);
        accountBalanceCache.add(genesis.getPublicKeyAddress(), genesisTransactionValue);
        BlockchainData.addAccountBalanceCache(blockchain.getId(), accountBalanceCache);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
    }

    private static void createAndRegisterSimpleTransactionRequest( Wallet walletA, Wallet walletB, List<AccountBasedTransactionRequest> transactionRequestsQueue, int value, AccountBalanceCache accountBalanceCache) throws BlockchainException {
        Optional<AccountBasedTransactionRequest> transactionRequestOptional = AccountBasedTransactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value, accountBalanceCache);
        if (transactionRequestOptional.isPresent()){
            AccountBasedTransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

    public static void simulate(Blockchain blockchain, AccountBalanceCache accountBalanceCache, int numBlocks, int difficulty) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        //Example transaction stream and processing
        List<AccountBasedTransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            createAndRegisterSimpleTransactionRequest(genesis, wallet, transactionRequestsQueue, 5, accountBalanceCache);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<AccountBasedTransactionRequests> transactionRequestsForNextBlock = constructTransactionRequestsForNextBlock(transactionRequestsQueue, blockchain);
            if (transactionRequestsForNextBlock.isPresent()) {
                mineNextBlock(transactionRequestsForNextBlock.get(), blockchain, difficulty, accountBalanceCache);
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }
        BlockchainData.addWallet(blockchain.getId(), wallet);
    }

}

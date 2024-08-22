package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainType;

import java.util.*;

public class MultiAccountBasedBlockchain {

    public static void mineNextBlock(AccountBasedTransactionRequests transactionRequests, String id, int difficulty) {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.MULTI_ACCOUNT, id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis =  mostRecentBlock == null;

        //Individual Transaction Verification
        if (!isGenesis) {
            for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
                boolean verified = AccountBasedTransactionVerification.verifySignature(transactionRequest, false, blockchain);
                if (!verified) {
                    return;
                }
            }
        }

        //Overall Verification (no repeat accounts)
        Set<String> accounts = new HashSet<>();
        for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            if (accounts.contains(transactionRequest.getPublicKeyAddress())){
                return;
            }
            accounts.add(transactionRequest.getPublicKeyAddress());
        }


        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        AccountBalanceCache accountBalanceCache = BlockchainData.getAccountBalanceCache(id);
        //Update Caches
        for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (AccountTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                accountBalanceCache.add(transactionOutput.getRecipient(), transactionOutput.getValue());
                if (!isGenesis) {
                    accountBalanceCache.subtract(transactionRequest.publicKeyAddress, transactionOutput.getValue());
                }
            }
        }
    }

    public static Optional<AccountBasedTransactionRequests> constructTransactionRequestsForNextBlock(List<AccountTransactionRequest> availableTransactionRequests, Blockchain blockchain) {
        List<AccountTransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (AccountTransactionRequest transactionRequest : availableTransactionRequests) {
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

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        BlockchainData.addBlockchain(BlockchainType.MULTI_ACCOUNT, blockchain);
        BlockchainData.addAccountBalanceCache(blockchain.getId(), new AccountBalanceCache());
        BlockchainData.addWalletCache(blockchain.getId());
    }

    public static void genesis(String id, long value) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        BlockchainData.addGenesisWallet(id, wallet);
        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(wallet.getPublicKeyAddress(), value);
        AccountBasedTransactionRequests requests = new AccountBasedTransactionRequests(List.of(new AccountTransactionRequest(null, List.of(transactionOutput))));
        mineNextBlock(requests, id, 1);
    }

    private static void createAndRegisterSimpleTransactionRequest(Wallet walletA, Wallet walletB, List<AccountTransactionRequest> transactionRequestsQueue, int value, String id) throws BlockchainException {
        Optional<AccountTransactionRequest> transactionRequestOptional = AccountBasedTransactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value, id);
        if (transactionRequestOptional.isPresent()){
            AccountTransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

    public static void simulate(String id, int numBlocks, int difficulty) throws BlockchainException {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.MULTI_ACCOUNT, id);

        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());

        //Example transaction stream and processing
        List<AccountTransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            createAndRegisterSimpleTransactionRequest(genesis, wallet, transactionRequestsQueue, 5, id);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<AccountBasedTransactionRequests> transactionRequestsForNextBlock = constructTransactionRequestsForNextBlock(transactionRequestsQueue, blockchain);
            if (transactionRequestsForNextBlock.isPresent()) {
                mineNextBlock(transactionRequestsForNextBlock.get(), id, difficulty);
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }
        BlockchainData.addWallet(blockchain.getId(), wallet);
    }


}

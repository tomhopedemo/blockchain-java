package crypto.blockchain.account;

import crypto.blockchain.*;

import java.util.*;

public class MultiAccountBasedBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public AccountBalanceCache accountBalanceCache;
    public AccountBasedTransactionVerification transactionVerification;

    public MultiAccountBasedBlockMining(Blockchain blockchain, int difficulty, AccountBalanceCache accountBalanceCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.accountBalanceCache = accountBalanceCache;
        this.transactionVerification = new AccountBasedTransactionVerification(accountBalanceCache);
    }

    public Optional<AccountBasedTransactionRequests> constructTransactionRequestsForNextBlock(List<AccountBasedTransactionRequest> availableTransactionRequests) {
        List<AccountBasedTransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (AccountBasedTransactionRequest transactionRequest : availableTransactionRequests) {
            //verify signature
            boolean verified = transactionVerification.verifySignature(transactionRequest, false);
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

    public void mineNextBlock(AccountBasedTransactionRequests transactionRequests) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis =  mostRecentBlock == null;

        //Individual Transaction Verification
        if (!isGenesis) {
            for (AccountBasedTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
                boolean verified = transactionVerification.verifySignature(transactionRequest, false);
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

}

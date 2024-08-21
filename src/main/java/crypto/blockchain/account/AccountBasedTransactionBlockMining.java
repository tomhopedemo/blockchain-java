package crypto.blockchain.account;

import crypto.blockchain.*;

public class AccountBasedTransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public AccountBalanceCache accountBalanceCache;
    public AccountBasedTransactionVerification transactionVerification;

    public AccountBasedTransactionBlockMining(Blockchain blockchain, int difficulty, AccountBalanceCache accountBalanceCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.accountBalanceCache = accountBalanceCache;
        this.transactionVerification = new AccountBasedTransactionVerification(accountBalanceCache);

    }

    public void mineNextBlock(AccountBasedTransactionRequest transactionRequest) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis = mostRecentBlock == null;
        //Verification on inputs
        if (!isGenesis){
            boolean verified = transactionVerification.verifySignature(transactionRequest, false);
            if (!verified){
                return;
            }
        }

        //Create block
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (AccountBasedTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            accountBalanceCache.add(transactionOutput.getRecipient(), transactionOutput.getValue());
            if (!isGenesis) {
                accountBalanceCache.subtract(transactionRequest.publicKeyAddress, transactionOutput.getValue());
            }
        }
    }


}

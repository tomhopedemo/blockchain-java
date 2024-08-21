package crypto.blockchain.utxo;

import crypto.blockchain.*;

public class TransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public TransactionCache transactionCache;
    public TransactionVerification transactionVerification;

    public TransactionBlockMining(Blockchain blockchain, int difficulty, TransactionCache transactionCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.transactionCache = transactionCache;
        this.transactionVerification = new TransactionVerification(transactionCache);

    }

    public void mineNextBlock(TransactionRequest transactionRequest) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null;

        //Verification on inputs
        boolean verified = transactionVerification.verifySignature(transactionRequest, skipEqualityCheck);
        if (!verified){
            return;
        }

        //Create block
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
        }
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            transactionCache.remove(transactionInput.getTransactionOutputHash());
        }

    }

}

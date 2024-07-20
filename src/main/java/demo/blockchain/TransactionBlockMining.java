package demo.blockchain;

import demo.objects.Block;

public class TransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;

    public TransactionBlockMining(Blockchain blockchain, int difficulty) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
    }

    public void mineNextBlock(TransactionRequest transactionRequest) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);
    }

}

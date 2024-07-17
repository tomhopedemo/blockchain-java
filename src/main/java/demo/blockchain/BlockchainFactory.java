package demo.blockchain;

import demo.objects.Block;

public class BlockchainFactory {

    int difficulty;
    int nextId = 0;

    public BlockchainFactory(int difficulty) {
        this.difficulty = difficulty;
    }

    public Blockchain createBlockchain() {
        String data = "abcde";
        String previousHash = "";
        Block block = new Block(data, previousHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineHash(difficulty);
        nextId++;
        Blockchain blockchain = new Blockchain(block, String.valueOf(nextId));
        return blockchain;
    }
}

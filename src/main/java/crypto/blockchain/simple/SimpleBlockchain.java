package crypto.blockchain.simple;

import crypto.blockchain.*;

public class SimpleBlockchain {

    public static void genesis(Blockchain blockchain, int difficulty) {
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);
    }

    public static void simulate(Blockchain blockchain, int numBlocks, int difficulty) {
        for (int i = 0; i < numBlocks; i++) {
            Block nextBlock = SimpleBlockMining.mineNextBlock(blockchain, difficulty);
            blockchain.add(nextBlock);
        }
    }

}
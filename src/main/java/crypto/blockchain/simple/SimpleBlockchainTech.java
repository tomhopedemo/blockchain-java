package crypto.blockchain.simple;

import crypto.blockchain.*;

public class SimpleBlockchainTech {

    public static Blockchain execute(String id, int difficulty) {
        return SimpleBlockchainFactory.createBlockchainWithGenesisBlock(id, difficulty);
    }

    public static Blockchain simulate(Blockchain blockchain, int numBlocks, int difficulty) {
        for (int i = 0; i < numBlocks; i++) {
            Block nextBlock = SimpleBlockMining.mineNextBlock(blockchain, difficulty);
            blockchain.add(nextBlock);
        }
        return blockchain;
    }


}
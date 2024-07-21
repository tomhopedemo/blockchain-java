package demo.blockchain.simple;

import demo.blockchain.Blockchain;
import demo.blockchain.BlockchainStore;
import demo.blockchain.Block;

public class SimpleSuperBlockMining {

    int difficulty;
    int numBlocksToMine;

    public SimpleSuperBlockMining(int numBlocksToMine, int difficulty) {
        this.numBlocksToMine = numBlocksToMine;
        this.difficulty = difficulty;
    }

    public void mine(BlockchainStore blockchainStore) throws Exception {
        for (int i = 0; i < numBlocksToMine; i++) {
            for (Blockchain blockchain : blockchainStore.blockchains) {
                SimpleBlockMining simpleBlockMining = new SimpleBlockMining(difficulty);
                Block nextBlock = simpleBlockMining.mineNextBlock(blockchain);
                blockchain.add(nextBlock);
            }
        }
    }

}

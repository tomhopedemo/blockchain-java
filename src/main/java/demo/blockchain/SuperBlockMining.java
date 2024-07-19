package demo.blockchain;

import demo.objects.Block;

public class SuperBlockMining {

    int difficulty;
    int numBlocksToMine;

    public SuperBlockMining(int numBlocksToMine, int difficulty) {
        this.numBlocksToMine = numBlocksToMine;
        this.difficulty = difficulty;
    }

    public void mine(BlockchainStore blockchainStore) throws Exception {
        for (int i = 0; i < numBlocksToMine; i++) {
            for (Blockchain blockchain : blockchainStore.blockchains) {
                BlockMining blockMining = new BlockMining(difficulty);
                Block nextBlock = blockMining.mineNextBlock(blockchain);
                blockchain.add(nextBlock);
            }
        }
    }

}

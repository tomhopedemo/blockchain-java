package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainType;

public class SimpleBlockchain {

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        BlockchainData.addBlockchain(BlockchainType.SIMPLE, blockchain);
    }

    public static void genesis(String id) {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.SIMPLE, id);
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);
    }

    public static void simulate(String id, int numBlocks, int difficulty) {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.SIMPLE, id);
        for (int i = 0; i < numBlocks; i++) {
            Block nextBlock = SimpleBlockMining.mineNextBlock(blockchain, difficulty);
            blockchain.add(nextBlock);
        }
    }

}
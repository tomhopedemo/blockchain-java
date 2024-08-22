package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainType;

import java.util.Random;

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
            Block nextBlock = mineNextBlock(blockchain, difficulty);
            blockchain.add(nextBlock);
        }
    }

    public static Block mineNextBlock(Blockchain blockchain, int difficulty) {
        Block mostRecentBlock = blockchain.getMostRecent();
        StringHashable data = constructData();
        Block nextBlock = new Block(data, mostRecentBlock.blockHashId);
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(difficulty));
        return nextBlock;
    }

    private static StringHashable constructData() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return new StringHashable(sb.toString());
    }

}
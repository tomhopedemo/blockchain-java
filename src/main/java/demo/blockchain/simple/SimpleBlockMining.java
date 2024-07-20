package demo.blockchain.simple;

import demo.blockchain.BlockMiner;
import demo.blockchain.Blockchain;
import demo.objects.Block;

import java.util.Random;

public class SimpleBlockMining {

    public int difficulty;

    public SimpleBlockMining(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block mineNextBlock(Blockchain blockchain) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String data = getData();
        Block nextBlock = new Block(data, data, mostRecentBlock.blockHashId);
        BlockMiner blockMiner = new BlockMiner(nextBlock);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        return nextBlock;
    }

    private static String getData() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

}

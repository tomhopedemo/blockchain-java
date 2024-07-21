package demo.blockchain.simple;

import demo.blockchain.BlockMiner;
import demo.blockchain.Blockchain;
import demo.blockchain.Block;

import java.util.Random;

public class SimpleBlockMining {

    public int difficulty;

    public SimpleBlockMining(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block mineNextBlock(Blockchain blockchain) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        StringHashable data = getData();
        Block nextBlock = new Block(data, mostRecentBlock.blockHashId);
        BlockMiner blockMiner = new BlockMiner(nextBlock);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        return nextBlock;
    }

    private static StringHashable getData() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return new StringHashable(sb.toString());
    }

}

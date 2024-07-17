package demo.blockchain;

import demo.objects.Block;

import java.util.Random;

public class BlockMining {

    public int difficulty;

    public BlockMining(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block mineNextBlock(Blockchain blockchain) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String data = getData();
        Block nextBlock = new Block(data, mostRecentBlock.hash);
        BlockMiner blockMiner = new BlockMiner(nextBlock);
        blockMiner.mineHash(difficulty);
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

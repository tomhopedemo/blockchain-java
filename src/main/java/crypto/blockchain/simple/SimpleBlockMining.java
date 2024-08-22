package crypto.blockchain.simple;

import crypto.blockchain.BlockMiner;
import crypto.blockchain.Blockchain;
import crypto.blockchain.Block;

import java.util.Random;

public class SimpleBlockMining {

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

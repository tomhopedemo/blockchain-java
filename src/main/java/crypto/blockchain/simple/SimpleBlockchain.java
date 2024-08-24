package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.api.Data;

import java.util.Random;

public record SimpleBlockchain (String id) {

    public void create(){
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
    }

    public void genesis() {
        Blockchain blockchain = Data.getBlockchain(id);
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);
    }

    public void simulate() {
        Blockchain blockchain = Data.getBlockchain(id);
        Block nextBlock = mineNextBlock(blockchain);
        blockchain.add(nextBlock);
    }

    public Block mineNextBlock(Blockchain blockchain) {
        Block mostRecentBlock = blockchain.getMostRecent();
        StringHashable data = constructData();
        Block nextBlock = new Block(data, mostRecentBlock.blockHashId);
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        return nextBlock;
    }

    private StringHashable constructData() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return new StringHashable(sb.toString());
    }

}
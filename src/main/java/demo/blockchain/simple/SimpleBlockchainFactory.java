package demo.blockchain.simple;

import demo.blockchain.BlockMiner;
import demo.blockchain.Blockchain;
import demo.blockchain.Block;

public class SimpleBlockchainFactory {

    int difficulty;

    public SimpleBlockchainFactory(int difficulty) {
        this.difficulty = difficulty;
    }

    public Blockchain createBlockchainWithGenesisBlock(String blockchainId)  {
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        Blockchain blockchain = new Blockchain(String.valueOf(blockchainId));
        blockchain.add(block);
        return blockchain;
    }
}

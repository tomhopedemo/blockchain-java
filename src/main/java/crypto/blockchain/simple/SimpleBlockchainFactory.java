package crypto.blockchain.simple;

import crypto.blockchain.BlockMiner;
import crypto.blockchain.Blockchain;
import crypto.blockchain.Block;

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

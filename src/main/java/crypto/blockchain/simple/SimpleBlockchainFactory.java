package crypto.blockchain.simple;

import crypto.blockchain.BlockMiner;
import crypto.blockchain.Blockchain;
import crypto.blockchain.Block;

public class SimpleBlockchainFactory {

    public static Blockchain createBlockchainWithGenesisBlock(String blockchainId, int difficulty)  {
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        Blockchain blockchain = new Blockchain(blockchainId);
        blockchain.add(block);
        return blockchain;
    }
}

package demo.blockchain;

import demo.objects.Block;

public class BlockchainFactory {

    int difficulty;
    int nextId = 0;

    public BlockchainFactory(int difficulty) {
        this.difficulty = difficulty;
    }

    public Blockchain createBlockchainWithGenesisBlock() throws Exception {
        String data = "abcde";
        String previousHash = "";
        Block block = new Block(data, data, previousHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineHash(difficulty);
        nextId++;
        Blockchain blockchain = new Blockchain(String.valueOf(nextId));
        blockchain.add(block);
        return blockchain;
    }
}

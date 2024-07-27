package demo.blockchain.simple;

import demo.blockchain.Blockchain;
import demo.blockchain.BlockchainStore;

public class SimpleBlockchainSuperFactory {

    int difficulty;
    int numBlockchains;

    public SimpleBlockchainSuperFactory(int difficulty, int numBlockchains) {
        this.difficulty = difficulty;
        this.numBlockchains = numBlockchains;
    }

    public BlockchainStore construct()  {
        SimpleBlockchainFactory blockchainFactory = new SimpleBlockchainFactory(difficulty);
        BlockchainStore blockchainStore = new BlockchainStore();
        for (int i = 0; i < numBlockchains; i++) {
            Blockchain blockchain = blockchainFactory.createBlockchainWithGenesisBlock(i);
            blockchainStore.add(blockchain);
        }
        return blockchainStore;
    }
}

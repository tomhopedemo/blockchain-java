package demo.blockchain;

public class BlockchainSuperFactory {

    int difficulty;
    int numBlockchains;

    public BlockchainSuperFactory(int difficulty, int numBlockchains) {
        this.difficulty = difficulty;
        this.numBlockchains = numBlockchains;
    }

    public BlockchainStore construct() throws Exception {
        BlockchainFactory blockchainFactory = new BlockchainFactory(difficulty);
        BlockchainStore blockchainStore = new BlockchainStore();
        for (int i = 0; i < numBlockchains; i++) {
            Blockchain blockchain = blockchainFactory.createBlockchainWithGenesisBlock();
            blockchainStore.add(blockchain);
        }
        return blockchainStore;
    }
}

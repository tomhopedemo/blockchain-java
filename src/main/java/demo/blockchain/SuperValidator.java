package demo.blockchain;

import demo.objects.Block;

public class SuperValidator {

    public BlockchainStore blockchainStore;

    public SuperValidator(BlockchainStore blockchainStore) {
        this.blockchainStore = blockchainStore;
    }

    public void validate() throws Exception {
        for (Blockchain blockchain : blockchainStore.blockchains) {
            for (int i = 0; i < blockchain.blocks.size(); i++) {
                Block block = blockchain.blocks.get(i);
                if (i > 0 && !block.previousBlockHashId.equals(blockchain.blocks.get(i -1).blockHashId)){
                    blockchain.valid = false;
                    break;
                }
                BlockHash hashCalculator = new BlockHash(block);
                if (!hashCalculator.validate()){
                    blockchain.valid = false;
                    break;
                }
            }
        }
    }
}

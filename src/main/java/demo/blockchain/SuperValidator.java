package demo.blockchain;

import demo.objects.Block;

public class SuperValidator {

    public BlockchainStore blockchainStore;

    public SuperValidator(BlockchainStore blockchainStore) {
        this.blockchainStore = blockchainStore;
    }

    public void validate(){
        for (Blockchain blockchain : blockchainStore.blockchains) {
            for (int i = 0; i < blockchain.blocks.size(); i++) {
                Block block = blockchain.blocks.get(i);
                if (i > 0 && !block.previousHash.equals(blockchain.blocks.get(i -1).hash)){
                    blockchain.valid = false;
                    break;
                }
                BlockHashCalculator hashCalculator = new BlockHashCalculator(block);
                if (!hashCalculator.validate()){
                    blockchain.valid = false;
                    break;
                }
            }
        }
    }
}

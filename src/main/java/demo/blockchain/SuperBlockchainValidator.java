package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

public class SuperBlockchainValidator {

    public BlockchainStore blockchainStore;

    public SuperBlockchainValidator(BlockchainStore blockchainStore) {
        this.blockchainStore = blockchainStore;
    }

    public void validate() throws Exception {
        for (Blockchain blockchain : blockchainStore.getBlockchains()) {
            for (int i = 0; i < blockchain.blocks.size(); i++) {
                Block block = blockchain.get(i);
                if (i > 0 && !block.getPreviousBlockHashId().equals(blockchain.get(i - 1).getBlockHashId())){
                    blockchain.valid = false;
                }

                byte[] blockHash = Hashing.hash(block.getPreHash());
                if (!Encoder.encodeToHexadecimal(blockHash).equals(block.getBlockHashId())){
                    blockchain.valid = false;
                }
            }
        }
    }
}

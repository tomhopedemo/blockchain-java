package demo.blockchain;

public class BlockchainValidator {

    public void validate(Blockchain blockchain) {
        for (int i = 0; i < blockchain.blocks.size(); i++) {
            Block block = blockchain.get(i);
            if (i > 0 && !block.getPreviousBlockHashId().equals(blockchain.get(i - 1).getBlockHashId())){
                blockchain.valid = false;
            }

            String blockHash  = block.calculateHash();
            if (!blockHash.equals(block.getBlockHashId())){
                blockchain.valid = false;
            }
        }
    }

}

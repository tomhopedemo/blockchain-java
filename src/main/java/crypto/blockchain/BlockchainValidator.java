package crypto.blockchain;

public class BlockchainValidator {

    public static void validate(Blockchain blockchain) throws BlockchainException {
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

        BlockchainSerialisation.checkSerializationStable(blockchain);
    }

}

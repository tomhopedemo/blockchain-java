package crypto;

public class ChainValidator {

    public static boolean validate(String id) {
        Blockchain chain = Caches.getChain(id);
        for (int i = 0; i < chain.blocks.size(); i++) {
            Block block = chain.get(i);
            if (i > 0 && !block.getPreviousBlockHashId().equals(chain.get(i - 1).getBlockHashId())){
                return false;
            }

            String blockHash  = block.calculateHash(Caches.getHashType(id));
            if (!blockHash.equals(block.getBlockHashId())){
                return false;
            }
        }
        return isSerializationStable(chain);
    }

    private static boolean isSerializationStable(Blockchain chain)  {
        String serialisedChain = ChainSerialisation.serialise(chain);
        Blockchain deserialisedChain = ChainSerialisation.deserialise(serialisedChain);
        return serialisedChain.equals(ChainSerialisation.serialise(deserialisedChain));
    }

}

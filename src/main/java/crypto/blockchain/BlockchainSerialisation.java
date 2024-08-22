package crypto.blockchain;

import com.google.gson.GsonBuilder;

public class BlockchainSerialisation {

    public static Blockchain deserialise(String serialisedBlockchain){
        return new GsonBuilder().setPrettyPrinting().create().fromJson(serialisedBlockchain, Blockchain.class);
    }

    public static String serialise(Blockchain blockchain){
        return new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    }

    public static void checkSerializationStable(Blockchain blockchain) throws BlockchainException {
        String serialisedBlockchain = serialise(blockchain);
        Blockchain deserialisedBlockchain = deserialise(serialisedBlockchain);
        boolean stable = serialisedBlockchain.equals(serialise(deserialisedBlockchain));
        if (!stable){
            throw new BlockchainException("Unstable Blockchain Serialization");
        }
    }
}

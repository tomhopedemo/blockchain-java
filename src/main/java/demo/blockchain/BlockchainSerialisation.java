package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlockchainSerialisation {


    public BlockchainSerialisation() {
    }

    public Blockchain deserialise(String serialisedBlockchain){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(serialisedBlockchain, Blockchain.class);
    }

    public String serialise(Blockchain blockchain){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(blockchain);
    }

    public void checkSerializationStable(Blockchain blockchain) throws BlockchainException {
        String serialisedBlockchain = serialise(blockchain);
        Blockchain deserialisedBlockchain = deserialise(serialisedBlockchain);
        boolean stable = serialisedBlockchain.equals(serialise(deserialisedBlockchain));
        if (!stable){
            throw new BlockchainException("Unstable Blockchain Serialization");
        }
    }
}

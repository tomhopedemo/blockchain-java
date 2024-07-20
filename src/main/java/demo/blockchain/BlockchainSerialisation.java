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

    public boolean checkSerializationStable(Blockchain blockchain){
        String serialisedBlockchain = serialise(blockchain);
        Blockchain deserialisedBlockchain = deserialise(serialisedBlockchain);
        return serialisedBlockchain.equals(serialise(deserialisedBlockchain));
    }
}

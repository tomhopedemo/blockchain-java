package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlockchainDeserialiser {

    String serialisedBlockchain;

    public BlockchainDeserialiser(String serialisedBlockchain) {
        this.serialisedBlockchain = serialisedBlockchain;
    }

    public Blockchain deserialise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(serialisedBlockchain, Blockchain.class);
    }
}

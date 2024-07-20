package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlockchainSerialiser {

    Blockchain blockchain;

    public BlockchainSerialiser(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public String serialise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(blockchain);
    }
}

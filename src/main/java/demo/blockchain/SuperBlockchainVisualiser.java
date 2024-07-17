package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SuperBlockchainVisualiser {

    BlockchainStore blockchainStore;

    public SuperBlockchainVisualiser(BlockchainStore blockchainStore) {
        this.blockchainStore = blockchainStore;
    }

    public void visualise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(blockchainStore);
        System.out.println(json);
    }
}

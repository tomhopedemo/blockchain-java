package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlockchainVisualiser {

    Blockchain blockchain;

    public BlockchainVisualiser(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void visualise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(blockchain);
        System.out.println(json);
    }
}

package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;

public record ChainService(String id) {

    static Gson JSON = new GsonBuilder().create();

    public void createChain() {
        Blockchain chain = new Blockchain(id);
        Data.addChain(chain);
    }

    public boolean hasChain() {
        return Data.hasChain(id);
    }

    public void allowBlockType(BlockType type) {
        Data.addType(id, type);
    }

    public Blockchain getChain(){
        return Data.getChain(id);
    }

    public String getChainJson(){
        return JSON.toJson(getChain());
    }

    public String getKeysJson() {
        return JSON.toJson(Data.getKeys(id));
    }

    public void submitRequest(Request request) {
        Requests.add(id, request);
    }

    public void requestMiner() {
        MinerPool.requestMiner(id);
    }

    public void disableAutoMining() {
        MinerPool.disable(id);
    }
}

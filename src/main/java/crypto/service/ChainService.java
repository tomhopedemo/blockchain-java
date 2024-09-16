package crypto.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.*;

public record ChainService(String id) {

    static Gson JSON = new GsonBuilder().create();

    public void createChain() {
        Blockchain chain = new Blockchain(id);
        Caches.addChain(chain);
    }

    public boolean hasChain() {
        return Caches.hasChain(id);
    }

    public void allowBlockType(BlockType type) {
        Caches.addType(id, type);
    }

    public Blockchain getChain(){
        return Caches.getChain(id);
    }

    public String getChainJson(){
        return JSON.toJson(getChain());
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

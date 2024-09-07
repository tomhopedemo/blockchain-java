package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountRequest;
import crypto.blockchain.currency.CurrencyRequest;
import crypto.blockchain.signed.SignedRequest;
import crypto.blockchain.utxo.UTXORequest;

public class ChainService {

    static Gson JSON = new GsonBuilder().create();

    public void createChain(String id) {
        Blockchain chain = new Blockchain(id);
        Data.addChain(chain);
    }

    public boolean hasChain(String id) {
        return Data.hasChain(id);
    }

    public void allowBlockType(String id, BlockType type) {
        Data.addType(id, type);
    }

    public Blockchain getChain(String id){
        return Data.getChain(id);
    }

    public String getChainJson(String id){
        return JSON.toJson(getChain(id));
    }

    public String getKeysJson(String id) {
        return JSON.toJson(Data.getKeys(id));
    }

    public void submitRequest(String id, BlockType type, Request request) {
        Requests.add(id, type, request);
    }

    public void requestMiner(String id) {
        MinerPool.requestMiner(id);
    }

    public void disableAutoMining(String id) {
        MinerPool.disable(id);
    }
}

package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.signed.SignedDataRequest;
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

    public Request deserialiseRequest(BlockType blockType, String requestJson) {
        return switch(blockType){
            case DATA -> JSON.fromJson(requestJson, DataRequest.class);
            case SIGNED_DATA -> JSON.fromJson(requestJson, SignedDataRequest.class);
            case ACCOUNT -> JSON.fromJson(requestJson, AccountTransactionRequest.class);
            case UTXO -> JSON.fromJson(requestJson, UTXORequest.class);
        };
    }

    public void requestMiner(String id) {
        MinerPool.requestMiner(id);
    }

    public void disableAutoMining(String id) {
        MinerPool.disable(id);
    }
}

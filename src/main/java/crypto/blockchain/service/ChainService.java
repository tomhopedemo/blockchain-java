package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountChain;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.signed.SignedChain;
import crypto.blockchain.signed.SignedDataRequest;
import crypto.blockchain.simple.SimpleChain;
import crypto.blockchain.utxo.UTXORequest;
import crypto.blockchain.utxo.UTXOChain;

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

    //currently not done by mining but should be.
    public void createGenesisBlock(String id, BlockType type, Object value, String key)  {
        switch(type){
            case DATA -> new SimpleChain(id).genesis((String) value);
            case SIGNED_DATA -> new SignedChain(id).genesis((String) value, key);
            case ACCOUNT -> new AccountChain(id).genesis((Long) value, key);
            case UTXO -> new UTXOChain(id).genesis((Long) value, key);
        }
        new Miner(id).run();
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

    public String createWalletJson(){
        return JSON.toJson(createWallet());
    }

    public Wallet createWallet() {
        return Wallet.generate();
    }

    public void submitRequest(String id, BlockType type, Request request) {
        switch(type){
            case DATA -> Requests.add(id, (DataRequest) request);
            case SIGNED_DATA -> Requests.add(id, (SignedDataRequest) request);
            case ACCOUNT -> Requests.add(id, (AccountTransactionRequest) request);
            case UTXO -> Requests.add(id, (UTXORequest) request);
        }
    }

    public Request deserialiseRequest(BlockType blockType, String requestJson) {
        return switch(blockType){
            case DATA -> JSON.fromJson(requestJson, DataRequest.class);
            case SIGNED_DATA -> JSON.fromJson(requestJson, SignedDataRequest.class);
            case ACCOUNT -> JSON.fromJson(requestJson, AccountTransactionRequest.class);
            case UTXO -> JSON.fromJson(requestJson, UTXORequest.class);
        };
    }

}

package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountChain;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.simple.SimpleBlockchain;
import crypto.blockchain.utxo.UTXORequest;
import crypto.blockchain.utxo.UTXOChain;

public class ChainService {

    public void createChain(String id) {
        Blockchain blockchain = new Blockchain(id);
        Data.addChain(blockchain);
    }

    public boolean hasChain(String id) {
        return Data.hasChain(id);
    }

    public void allowBlockType(String id, BlockType type) {
        Data.addType(id, type);
    }

    public void createGenesisBlock(String id, BlockType type, Long value, String key)  {
        switch(type){
            case DATA -> new SimpleBlockchain(id).genesis();
            case ACCOUNT -> new AccountChain(id).genesis(value, key);
            case UTXO -> new UTXOChain(id).genesis(value, key);
        }
    }

    public void simulateBlock(String id, BlockType type, Wallet from) throws BlockchainException {
        switch(type){
            case DATA -> new SimpleBlockchain(id).simulate();
            case ACCOUNT -> new AccountChain(id).simulate();
            case UTXO -> new UTXOChain(id).simulate(from);
        }
    }

    public Blockchain getChain(String id){
        return Data.getChain(id);
    }

    public String getChainJson(String id){
        return new GsonBuilder().create().toJson(getChain(id));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public String getKeysJson(String id) {
        return new GsonBuilder().create().toJson(Data.getKeys(id));
    }

    public String createWalletJson(){
        return new GsonBuilder().create().toJson(createWallet());
    }

    public Wallet createWallet() {
        return Wallet.generate();
    }

    public void submitRequest(String id, BlockType type, Request request) {
        switch(type){
            case DATA -> {
                Requests.add(id, (DataRequest) request);
            }
            case ACCOUNT -> {
                Requests.add(id, (AccountTransactionRequest) request);
            }
            case UTXO -> {
                Requests.add(id, (UTXORequest) request);
            }
        }
    }

    public Request deserialiseRequest(BlockType blockType, String requestJson) {
        final Gson gson = new GsonBuilder().create();
        return switch(blockType){
            case DATA -> gson.fromJson(requestJson, DataRequest.class);
            case ACCOUNT -> gson.fromJson(requestJson, AccountTransactionRequest.class);
            case UTXO -> gson.fromJson(requestJson, UTXORequest.class);
        };
    }

}

package crypto.blockchain.service;

import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.MultiAccountChain;
import crypto.blockchain.api.ChainType;
import crypto.blockchain.simple.SimpleBlockchain;
import crypto.blockchain.utxo.MultiTransactionChain;

public class ChainService {

    public void createBlockchain(String id) {
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
    }

    public void allowBlockType(String id, BlockType type) {
        Data.addType(id, type);
    }

    public void oldStyleCreate(ChainType type, String id) {
        switch(type) {
            case SIMPLE -> new SimpleBlockchain(id).create();
            case MULTI_ACCOUNT -> new MultiAccountChain(id).create();
            case MULTI_UTXO -> new MultiTransactionChain(id).create();
            case COMBO -> new ComboChain(id).create();
        }
    }

    public void createGenesisBlock(String id, ChainType type, Long value, String key) throws BlockchainException {
        switch(type){
            case SIMPLE -> new SimpleBlockchain(id).genesis();
            case MULTI_ACCOUNT -> new MultiAccountChain(id).genesis(value, key);
            case MULTI_UTXO -> new MultiTransactionChain(id).genesis(value, key);
            case COMBO -> new ComboChain(id).genesis(value, key);
        }
    }

    public void simulateBlock(ChainType type, String id, Wallet from) throws BlockchainException {
        switch(type){
            case SIMPLE -> new SimpleBlockchain(id).simulate();
            case MULTI_ACCOUNT -> new MultiAccountChain(id).simulate();
            case MULTI_UTXO -> new MultiTransactionChain(id).simulate();
            case COMBO -> new ComboChain(id).simulate(from);
        };
    }

    public Blockchain getBlockchain(String id){
        return Data.getBlockchain(id);
    }

    public String getBlockchainJson(String id){
        return new GsonBuilder().create().toJson(getBlockchain(id));
    }

    public boolean exists(String id){
        return Data.getBlockchain(id) != null;
    }

    public String getKeysJson(String id) {
        return new GsonBuilder().create().toJson(Data.getKeys(id));
    }

    public String createWalletJson(){
        return new GsonBuilder().create().toJson(Wallet.generate());
    }
}

package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.ComboBlockchain;
import crypto.blockchain.Wallet;
import crypto.blockchain.account.AccountChain;
import crypto.blockchain.account.MultiAccountChain;
import crypto.blockchain.simple.SimpleBlockchain;
import crypto.blockchain.utxo.MultiTransactionChain;
import crypto.blockchain.utxo.TransactionChain;
import org.springframework.stereotype.Component;

@Component
public class BlockchainService {

    public void createBlockchain(String id, BlockchainType type) throws BlockchainException {
        switch(type){
            case SIMPLE -> new SimpleBlockchain(id).create();
            case ACCOUNT -> new AccountChain(id).create();
            case MULTI_ACCOUNT -> new MultiAccountChain(id).create();
            case UTXO -> new TransactionChain(id).create();
            case MULTI_UTXO -> new MultiTransactionChain(id).create();
            case COMBO -> new ComboBlockchain(id).create();
        }
    }

    public Blockchain createGenesisBlock(String id, BlockchainType type, Long value, String key) throws BlockchainException {
        switch(type){
            case SIMPLE -> new SimpleBlockchain(id).genesis();
            case ACCOUNT -> new AccountChain(id).genesis(value, key);
            case MULTI_ACCOUNT -> new MultiAccountChain(id).genesis(value, key);
            case UTXO ->  new TransactionChain(id).genesis(value, key);
            case MULTI_UTXO -> new MultiTransactionChain(id).genesis(value, key);
            case COMBO -> new ComboBlockchain(id).genesis(value, key);
        }
        return Data.getBlockchain(id);
    }

    public Blockchain simulateBlock(BlockchainType type, String id, Wallet from) throws BlockchainException {
        switch(type){
            case SIMPLE -> new SimpleBlockchain(id).simulate();
            case ACCOUNT -> new AccountChain(id).simulate(from);
            case MULTI_ACCOUNT -> new MultiAccountChain(id).simulate();
            case UTXO -> new TransactionChain(id).simulate();
            case MULTI_UTXO -> new MultiTransactionChain(id).simulate();
            case COMBO -> new ComboBlockchain(id).simulate(from);
        };
        return Data.getBlockchain(id);
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
}

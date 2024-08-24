package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.ComboBlockchain;
import crypto.blockchain.Wallet;
import crypto.blockchain.account.AccountBlockchain;
import crypto.blockchain.account.MultiAccountBasedBlockchain;
import crypto.blockchain.simple.SimpleBlockchain;
import crypto.blockchain.utxo.MultiTransactionalBlockchain;
import crypto.blockchain.utxo.TransactionBlockchain;

public class BlockchainService {

    public static Blockchain getBlockchain(String id){
        return Data.getBlockchain(id);
    }

    public static Blockchain createBlockchain(String id, BlockchainType type) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.create(id);
            case ACCOUNT -> new AccountBlockchain(id).create();
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.create(id);
            case UTXO -> TransactionBlockchain.create(id);
            case MULTI_UTXO -> MultiTransactionalBlockchain.create(id);
            case COMBO -> new ComboBlockchain(id).create();
        }
        return Data.getBlockchain(id);
    }

    public static Blockchain createGenesisBlock(String id, BlockchainType type, Long value, String key) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.genesis(id);
            case ACCOUNT -> new AccountBlockchain(id).genesis(value, key);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.genesis(id, value, key);
            case UTXO ->  TransactionBlockchain.genesis(id, value, key);
            case MULTI_UTXO -> MultiTransactionalBlockchain.genesis(id, value, key);
            case COMBO -> new ComboBlockchain(id).genesis(value, key);
        }
        return Data.getBlockchain(id);
    }

    public static Blockchain simulateBlock(BlockchainType type, String id, Wallet from) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.simulate(id);
            case ACCOUNT -> new AccountBlockchain(id).simulate(from);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.simulate(id);
            case UTXO -> TransactionBlockchain.simulate(id);
            case MULTI_UTXO -> MultiTransactionalBlockchain.simulate(id);
            case COMBO -> new ComboBlockchain(id).simulate(from);
        };
        return Data.getBlockchain(id);
    }
}

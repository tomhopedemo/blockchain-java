package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.ComboBlockchain;
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
            case ACCOUNT -> AccountBlockchain.create(id);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.create(id);
            case UTXO -> TransactionBlockchain.create(id);
            case MULTI_UTXO -> MultiTransactionalBlockchain.create(id);
            case COMBO -> ComboBlockchain.create(id);
        }
        return Data.getBlockchain(id);
    }

    public static Blockchain createGenesisBlock(String id, BlockchainType type, Long genesisValue) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.genesis(id);
            case ACCOUNT -> AccountBlockchain.genesis(id, genesisValue);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.genesis(id, genesisValue);
            case UTXO ->  TransactionBlockchain.genesis(id, genesisValue);
            case MULTI_UTXO -> MultiTransactionalBlockchain.genesis(id, genesisValue);
        }
        return Data.getBlockchain(id);
    }

    public static Blockchain simulateBlocks(BlockchainType type, String id, int numBlocks, int difficulty) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.simulate(id, numBlocks, difficulty);
            case ACCOUNT -> AccountBlockchain.simulate(id, numBlocks, difficulty);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.simulate(id, numBlocks, difficulty);
            case UTXO -> TransactionBlockchain.simulate(id, difficulty);
            case MULTI_UTXO -> MultiTransactionalBlockchain.simulate(id, numBlocks, difficulty);
        };
        return Data.getBlockchain(id);
    }
}

package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.Wallet;
import crypto.blockchain.account.AccountBasedBlockchain;
import crypto.blockchain.account.MultiAccountBasedBlockchain;
import crypto.blockchain.simple.SimpleBlockchain;
import crypto.blockchain.utxo.MultiTransactionalBlockchain;
import crypto.blockchain.utxo.TransactionalBlockchain;

import java.util.Objects;

import static crypto.blockchain.api.BlockchainType.MULTI_UTXO;

public class BlockchainService {

    public static Blockchain getBlockchain(BlockchainType type, String id){
        return BlockchainData.getBlockchain(type, id);
    }

    public static Blockchain createBlockchain(String id, BlockchainType type) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.create(id);
            case ACCOUNT -> AccountBasedBlockchain.create(id);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.create(id);
            case UTXO -> TransactionalBlockchain.create(id);
            case MULTI_UTXO -> MultiTransactionalBlockchain.create(id);
        }
        return BlockchainData.getBlockchain(type, id);
    }

    public static Blockchain createGenesisBlock(String id, BlockchainType type, Long genesisValue) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.genesis(id);
            case ACCOUNT -> AccountBasedBlockchain.genesis(id, genesisValue);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.genesis(id, genesisValue);
            case UTXO ->  TransactionalBlockchain.genesis(id, genesisValue);
            case MULTI_UTXO -> MultiTransactionalBlockchain.genesis(id, genesisValue);
        }
        return BlockchainData.getBlockchain(type, id);
    }

    public static Blockchain simulateBlocks(BlockchainType type, String id, int numBlocks, int difficulty) throws BlockchainException {
        switch(type){
            case SIMPLE -> SimpleBlockchain.simulate(id, numBlocks, difficulty);
            case ACCOUNT -> AccountBasedBlockchain.simulate(id, numBlocks, difficulty);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.simulate(id, numBlocks, difficulty);
            case UTXO -> TransactionalBlockchain.simulate(id, difficulty);
            case MULTI_UTXO -> MultiTransactionalBlockchain.simulate(id, numBlocks, difficulty);
        };
        return BlockchainData.getBlockchain(type, id);
    }
}

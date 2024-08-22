package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.account.AccountBasedBlockchainTech;
import crypto.blockchain.account.MultiAccountBasedBlockchainTech;
import crypto.blockchain.simple.SimpleBlockchainTech;
import crypto.blockchain.utxo.MultiTransactionalBlockchainTech;
import crypto.blockchain.utxo.TransactionalBlockchainTech;

public class BlockchainService {

    public static Blockchain getBlockchain(BlockchainType type, String id){
        return BlockchainData.getBlockchain(type, id);
    }

    public static Blockchain createBlockchain(String id, BlockchainType type, int difficulty, Long genesisValue) throws BlockchainException {
        Blockchain blockchain = switch(type){
            case SIMPLE -> SimpleBlockchainTech.execute(id, difficulty);
            case ACCOUNT -> AccountBasedBlockchainTech.execute(id, difficulty, genesisValue);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchainTech.execute(id, difficulty, genesisValue);
            case UTXO -> TransactionalBlockchainTech.execute(id, difficulty, genesisValue);
            case MULTI_UTXO -> MultiTransactionalBlockchainTech.execute(id, difficulty, genesisValue);
        };
        BlockchainData.addBlockchain(type, blockchain);
        return blockchain;
    }

    public static Blockchain simulateBlocks(BlockchainType blockchainType, String id, int numBlocks, int difficulty) throws BlockchainException {
        Blockchain blockchain = BlockchainData.getBlockchain(blockchainType, id);
        Blockchain updatedBlockchain = switch(blockchainType){
            case SIMPLE -> SimpleBlockchainTech.simulate(blockchain, numBlocks, difficulty);
            case ACCOUNT -> AccountBasedBlockchainTech.simulate(blockchain, BlockchainData.getAccountBalanceCache(blockchain.getId()), numBlocks, difficulty);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchainTech.simulate(blockchain, BlockchainData.getAccountBalanceCache(id), numBlocks, difficulty);
            case UTXO -> TransactionalBlockchainTech.simulate(blockchain, BlockchainData.getTransactionCache(id), numBlocks, difficulty);
            case MULTI_UTXO -> MultiTransactionalBlockchainTech.simulate(blockchain, BlockchainData.getTransactionCache(id), numBlocks, difficulty);
        };
        return updatedBlockchain;
    }
}

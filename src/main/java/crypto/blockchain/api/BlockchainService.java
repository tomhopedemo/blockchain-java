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

public class BlockchainService {

    public static Blockchain getBlockchain(BlockchainType type, String id){
        return BlockchainData.getBlockchain(type, id);
    }

    public static Blockchain createBlockchain(String id, BlockchainType type, int difficulty, Long genesisValue) throws BlockchainException {
        Blockchain blockchain = new Blockchain(id);
        BlockchainData.addBlockchain(type, blockchain);
        Wallet genesis = Wallet.generate();
        switch(type){
            case SIMPLE -> SimpleBlockchain.genesis(blockchain, difficulty);
            case ACCOUNT -> AccountBasedBlockchain.genesis(blockchain, difficulty, genesisValue, genesis);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.genesis(blockchain, difficulty, genesisValue, genesis);
            case UTXO -> TransactionalBlockchain.genesis(blockchain, difficulty, genesisValue, genesis);
            case MULTI_UTXO -> MultiTransactionalBlockchain.genesis(blockchain, difficulty, genesisValue, genesis);
        }
        return blockchain;
    }

    public static Blockchain simulateBlocks(BlockchainType blockchainType, String id, int numBlocks, int difficulty) throws BlockchainException {
        Blockchain blockchain = BlockchainData.getBlockchain(blockchainType, id);
        switch(blockchainType){
            case SIMPLE -> SimpleBlockchain.simulate(blockchain, numBlocks, difficulty);
            case ACCOUNT -> AccountBasedBlockchain.simulate(blockchain, BlockchainData.getAccountBalanceCache(blockchain.getId()), numBlocks, difficulty);
            case MULTI_ACCOUNT -> MultiAccountBasedBlockchain.simulate(blockchain, BlockchainData.getAccountBalanceCache(id), numBlocks, difficulty);
            case UTXO -> TransactionalBlockchain.simulate(blockchain, BlockchainData.getTransactionCache(id), numBlocks, difficulty);
            case MULTI_UTXO -> MultiTransactionalBlockchain.simulate(blockchain, BlockchainData.getTransactionCache(id), numBlocks, difficulty);
        };
        return blockchain;
    }
}

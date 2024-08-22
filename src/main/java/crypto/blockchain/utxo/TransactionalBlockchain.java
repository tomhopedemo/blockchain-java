package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainType;

public class TransactionalBlockchain {

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        BlockchainData.addBlockchain(BlockchainType.UTXO, blockchain);
        BlockchainData.addWalletCache(id);
        BlockchainData.addTransactionCache(id);
    }

    public static void genesis(String id, long value) {
        Wallet genesis = Wallet.generate();
        BlockchainData.addGenesisWallet(id, genesis);
        TransactionCache transactionCache = BlockchainData.getTransactionCache(id);
        TransactionRequest genesisTransactionRequest = TransactionRequestFactory.genesisTransaction(genesis, value, transactionCache);
        mineNextBlock(genesisTransactionRequest, id, 1);
    }

    public static void simulate(String id, int difficulty) {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(id);
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.UTXO, id);
        TransactionCache transactionCache = BlockchainData.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.createTransactionRequest(genesis, wallet.getPublicKeyAddress(), 5, transactionCache).get();
        mineNextBlock(transactionRequest, id, difficulty);
        BlockchainData.addWallet(blockchain.getId(), wallet);
    }

    public static void mineNextBlock(TransactionRequest transactionRequest, String id, int difficulty) {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.UTXO, id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null;

        //Verification on inputs
        boolean verified = TransactionVerification.verifySignature(transactionRequest, skipEqualityCheck, id);
        if (!verified){
            return;
        }

        //Create block
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        TransactionCache transactionCache = BlockchainData.getTransactionCache(id);
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
        }
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            transactionCache.remove(transactionInput.getTransactionOutputHash());
        }

    }

}

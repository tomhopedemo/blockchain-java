package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.Data;

public class TransactionBlockchain {

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
        Data.addWalletCache(id);
        Data.addTransactionCache(id);
    }

    public static void genesis(String id, long value, String genesisKey) {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest genesisTransactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        mineNextBlock(genesisTransactionRequest, id);
    }

    public static void simulate(String id) {
        Wallet wallet = Wallet.generate();
        Wallet genesis = Data.getGenesisWallet(id);
        Blockchain blockchain = Data.getBlockchain(id);
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.createTransactionRequest(genesis, wallet.getPublicKeyAddress(), 5, transactionCache).get();
        mineNextBlock(transactionRequest, id);
        Data.addWallet(blockchain.getId(), wallet);
    }

    public static void mineNextBlock(TransactionRequest transactionRequest, String id) {
        Blockchain blockchain = Data.getBlockchain(id);
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
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);

        //Update Caches
        TransactionCache transactionCache = Data.getTransactionCache(id);
        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
        }
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            transactionCache.remove(transactionInput.getTransactionOutputHash());
        }

    }

}

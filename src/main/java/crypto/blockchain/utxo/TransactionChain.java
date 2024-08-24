package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.Data;

public record TransactionChain(String id) {

    public void create(){
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
        Data.addWalletCache(id);
        Data.addTransactionCache(id);
    }

    public void genesis(long value, String genesisKey) {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest genesisTransactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        mineNextBlock(genesisTransactionRequest);
    }

    public void simulate() {
        Wallet wallet = Wallet.generate();
        Wallet genesis = Data.getGenesisWallet(id);
        Blockchain blockchain = Data.getBlockchain(id);
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.createTransactionRequest(genesis, wallet.getPublicKeyAddress(), 5, transactionCache).get();
        mineNextBlock(transactionRequest);
        Data.addWallet(blockchain.getId(), wallet);
    }

    public void mineNextBlock(TransactionRequest transactionRequest) {
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

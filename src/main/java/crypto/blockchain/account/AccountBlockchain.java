package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.Data;

import java.util.List;

public record AccountBlockchain (String id){

    public void create(){
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
        Data.addWalletCache(id);
        Data.addAccountBalanceCache(id);
    }

    public void genesis(long value, String genesisKey) throws BlockchainException {
        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(genesisKey, value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        mineNextBlock(request, 1);
    }

    public void simulate(Wallet from) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Data.addWallet(id, wallet);
        AccountTransactionRequest transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(from, wallet.getPublicKeyAddress(), 5, id).get();
        mineNextBlock(transactionRequest, 1);
    }

    public void mineNextBlock(AccountTransactionRequest transactionRequest, int difficulty) {
        Blockchain blockchain = Data.getBlockchain(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis = mostRecentBlock == null;
        //Verification on inputs
        if (!isGenesis){
            boolean verified = AccountTransactionVerification.verifySignature(transactionRequest, false, id);
            if (!verified){
                return;
            }
        }
        //Create block
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (AccountTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            Data.getAccountBalanceCache(blockchain.getId()).add(transactionOutput.getRecipient(), transactionOutput.getValue());
            if (!isGenesis) {
                Data.getAccountBalanceCache(blockchain.getId()).subtract(transactionRequest.getPublicKeyAddress(), transactionOutput.getValue());
            }
        }
    }

}

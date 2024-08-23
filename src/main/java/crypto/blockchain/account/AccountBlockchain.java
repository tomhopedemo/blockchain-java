package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.Data;

import java.util.List;

public class AccountBlockchain {

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        Data.addBlockchain(blockchain);
        Data.addWalletCache(blockchain.getId());
        Data.addAccountBalanceCache(blockchain.getId());
    }

    public static void genesis(String id, long value, String genesisKey) throws BlockchainException {
        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(genesisKey, value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        mineNextBlock(request, id, 1);
    }

    public static void simulate(String id, int numBlocks, int difficulty, Wallet from) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Data.addWallet(id, wallet);
        for (int i = 0; i < numBlocks; i++) {
            AccountTransactionRequest transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(from, wallet.getPublicKeyAddress(), 5, id).get();
            mineNextBlock(transactionRequest, id, difficulty);
        }
    }

    public static void mineNextBlock(AccountTransactionRequest transactionRequest, String id, int difficulty) {
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

package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;

public class AccountBasedBlockchain {

    public static void mineNextBlock(AccountBasedTransactionRequest transactionRequest, Blockchain blockchain, int difficulty) {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis = mostRecentBlock == null;
        //Verification on inputs
        if (!isGenesis){
            boolean verified = AccountBasedTransactionVerification.verifySignature(transactionRequest, false, blockchain);
            if (!verified){
                return;
            }
        }

        //Create block
        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (AccountBasedTransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            BlockchainData.getAccountBalanceCache(blockchain.getId()).add(transactionOutput.getRecipient(), transactionOutput.getValue());
            if (!isGenesis) {
                BlockchainData.getAccountBalanceCache(blockchain.getId()).subtract(transactionRequest.publicKeyAddress, transactionOutput.getValue());
            }
        }
    }

    public static void genesis(Blockchain blockchain, int difficulty, long genesisTransactionValue, Wallet genesis) throws BlockchainException {
        BlockchainData.addAccountBalanceCache(blockchain.getId(), new AccountBalanceCache());
        BlockchainData.addWalletCache(blockchain.getId());
        AccountBasedTransactionRequest genesisTransactionRequest = AccountBasedTransactionRequestFactory.genesisTransaction(genesis, genesisTransactionValue);
        mineNextBlock(genesisTransactionRequest, blockchain, difficulty);
        BlockchainData.addAccountBalance(blockchain.getId(), genesis, genesisTransactionValue);
        BlockchainData.addGenesisWallet(blockchain.getId(), genesis);
    }

    public static void simulate(Blockchain blockchain, AccountBalanceCache accountBalanceCache, int numBlocks, int difficulty) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Wallet genesis = BlockchainData.getGenesisWallet(blockchain.getId());
        for (int i = 0; i < numBlocks; i++) {
            AccountBasedTransactionRequest transactionRequest = AccountBasedTransactionRequestFactory.createTransactionRequest(genesis, wallet.publicKeyAddress, 5, accountBalanceCache).get();
            mineNextBlock(transactionRequest, blockchain, difficulty);
        }
        BlockchainData.addWallet(blockchain.getId(), wallet);
    }
}

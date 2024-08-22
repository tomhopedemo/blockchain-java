package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.blockchain.api.BlockchainType;

import java.util.List;

public class AccountBasedBlockchain {

    public static void create(String id){
        Blockchain blockchain = new Blockchain(id);
        BlockchainData.addBlockchain(BlockchainType.ACCOUNT, blockchain);
        BlockchainData.addWalletCache(blockchain.getId());
        BlockchainData.addAccountBalanceCache(blockchain.getId());
    }

    public static void genesis(String id, long value) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        BlockchainData.addGenesisWallet(id, wallet);
        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(wallet.getPublicKeyAddress(), value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        mineNextBlock(request, id, 1);
    }

    public static void simulate(String id, int numBlocks, int difficulty) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        BlockchainData.addWallet(id, wallet);
        Wallet genesis = BlockchainData.getGenesisWallet(id);
        for (int i = 0; i < numBlocks; i++) {
            AccountTransactionRequest transactionRequest = AccountBasedTransactionRequestFactory.createTransactionRequest(genesis, wallet.publicKeyAddress, 5, id).get();
            mineNextBlock(transactionRequest, id, difficulty);
        }
    }

    public static void mineNextBlock(AccountTransactionRequest transactionRequest, String id, int difficulty) {
        Blockchain blockchain = BlockchainData.getBlockchain(BlockchainType.ACCOUNT, id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis = mostRecentBlock == null;
        //Verification on inputs
        if (!isGenesis){
            boolean verified = AccountBasedTransactionVerification.verifySignature(transactionRequest, false, id);
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
            BlockchainData.getAccountBalanceCache(blockchain.getId()).add(transactionOutput.getRecipient(), transactionOutput.getValue());
            if (!isGenesis) {
                BlockchainData.getAccountBalanceCache(blockchain.getId()).subtract(transactionRequest.getPublicKeyAddress(), transactionOutput.getValue());
            }
        }
    }

}

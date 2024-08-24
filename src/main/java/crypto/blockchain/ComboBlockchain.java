package crypto.blockchain;

import crypto.blockchain.account.AccountBlockchain;
import crypto.blockchain.account.AccountTransactionOutput;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.api.Data;
import crypto.blockchain.utxo.*;

import java.util.List;

public record ComboBlockchain (String id){

    public void create(){
        Data.addBlockchain(new Blockchain(id));
        Data.addTransactionCache(id);
        Data.addAccountBalanceCache(id);
        Data.addWalletCache(id);
    }

    public void genesis(long value, String genesisKey) throws BlockchainException {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        TransactionBlockchain.mineNextBlock(transactionRequest, id);

        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(genesisKey, value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        new AccountBlockchain(id).mineNextBlock(request, 1);
    }

    public void simulate(Wallet from) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Data.addWallet(id, wallet);
        AccountTransactionRequest transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(from, wallet.getPublicKeyAddress(), 5, id).get();
        new AccountBlockchain(id).mineNextBlock(transactionRequest, 1);
    }

}

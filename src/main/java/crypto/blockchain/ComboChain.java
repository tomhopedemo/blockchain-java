package crypto.blockchain;

import crypto.blockchain.account.AccountChain;
import crypto.blockchain.account.AccountTransactionOutput;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.utxo.*;

import java.util.List;

public record ComboChain(String id){

    public void create(){
        Data.addBlockchain(new Blockchain(id));
        Data.addTransactionCache(id);
        Data.addAccountBalanceCache(id);
        Data.addWalletCache(id);
    }

    public void genesis(long value, String genesisKey) throws BlockchainException {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        new TransactionChain(id).mineNextBlock(transactionRequest);

        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(genesisKey, value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        new AccountChain(id).mineNextBlock(request, 1);
    }

    public void simulate(Wallet from) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Data.addWallet(id, wallet);
        AccountTransactionRequest transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(from, wallet.getPublicKeyAddress(), 5, id).get();
        new AccountChain(id).mineNextBlock(transactionRequest, 1);
    }

}

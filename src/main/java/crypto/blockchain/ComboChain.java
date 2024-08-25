package crypto.blockchain;

import crypto.blockchain.account.*;
import crypto.blockchain.utxo.*;

import java.util.List;

public record ComboChain(String id){

    public void genesis(long value, String genesisKey) {
        TransactionRequest transactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, id);
        new UTXOChain(id).mineNextBlock(new TransactionRequests(List.of(transactionRequest)), id, 1);

        AccountTransactionOutput transactionOutput = new AccountTransactionOutput(genesisKey, value);
        AccountTransactionRequest request = new AccountTransactionRequest(null, List.of(transactionOutput));
        new AccountChain(id).mineNextBlock(new AccountTransactionRequests(List.of(request)), id);
    }

    public void simulate(Wallet from) throws BlockchainException {
        Wallet wallet = Wallet.generate();
        Data.addWallet(id, wallet);
        AccountTransactionRequest transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(from, wallet.getPublicKeyAddress(), 5, id).get();
        new AccountChain(id).mineNextBlock(new AccountTransactionRequests(List.of(transactionRequest)), id);
    }

}

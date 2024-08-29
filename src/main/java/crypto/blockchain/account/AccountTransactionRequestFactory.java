package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;


public class AccountTransactionRequestFactory {

    public static Optional<AccountTransactionRequest> createTransactionRequest(Wallet wallet, String recipient, long transactionValue, String id) throws ChainException {
        Long balance = Data.getAccountBalance(id, wallet.getPublicKeyAddress());
        if (balance < transactionValue) {
            return Optional.empty();
        }

        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(recipient, transactionValue));
        return Optional.of(AccountTransactionRequest.create(wallet, transactionOutputs));
    }

}

package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.blockchain.api.data.TransactionalRequestParams;
import crypto.encoding.Encoder;

import java.util.*;


public class AccountTransactionRequestFactory {

    public static Optional<AccountTransactionRequest> createTransactionRequest(String id, TransactionalRequestParams transactionalRequestParams) throws ChainException {
        Optional<KeyPair> keyPair = Data.getKeyPair(id, transactionalRequestParams.from());
        if (keyPair.isEmpty()){
            return Optional.empty();
        }
        Long balance = Data.getAccountBalance(id, transactionalRequestParams.currency(), keyPair.get().getPublicKeyAddress());
        if (balance < transactionalRequestParams.value()) {
            return Optional.empty();
        }

        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(transactionalRequestParams.to(), transactionalRequestParams.value()));
        AccountTransactionRequest accountTransactionRequest = create(keyPair.get(), transactionalRequestParams.currency(), transactionOutputs);
        return Optional.of(accountTransactionRequest);
    }

    public static AccountTransactionRequest create(KeyPair keyPair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String publicKeyAddress = keyPair.getPublicKeyAddress();
        String hash = AccountTransactionRequest.generateHash(keyPair.getPublicKeyAddress(), currency, transactionOutputs);
        byte[] signature = Signing.sign(keyPair, hash);
        return new AccountTransactionRequest(publicKeyAddress, currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }

    public static AccountTransactionRequest createGenesis(String id, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        Optional<CurrencyRequest> found = Data.getCurrency(id, currency);
        if (found.isEmpty()){
            throw new ChainException("CURRENCY NOT FOUND");
        }
        CurrencyRequest currencyRequest = found.get();
        KeyPair keyPair = new KeyPair(currencyRequest.privateKey(), currencyRequest.publicKey());
        return create(keyPair, currency, transactionOutputs);
    }

}

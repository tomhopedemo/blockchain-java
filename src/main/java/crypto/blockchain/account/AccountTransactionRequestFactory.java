package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.encoding.Encoder;

import java.util.*;


public class AccountTransactionRequestFactory {

    public static Optional<AccountTransactionRequest> createTransactionRequest(KeyPair keyPair, String currency, String recipient, long transactionValue, String id) throws ChainException {
        Long balance = Data.getAccountBalance(id, currency, keyPair.getPublicKeyAddress());
        if (balance < transactionValue) {
            return Optional.empty();
        }

        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(recipient, transactionValue));
        AccountTransactionRequest accountTransactionRequest = create(keyPair, currency, transactionOutputs);
        return Optional.of(accountTransactionRequest);
    }

    //alternatively could check for equality between from and to. alt an indication of genesis.
    //i think an indication of genesis is better - we can indicate it if we have an additional
    //chain of keypairs data. if the public key is in this keypairs chain, then it is a genesis/minting account.
    //there will be strict rules in the system to indicate what minting is allowed and when.
    //so this will be a public key which is known.
    public static AccountTransactionRequest create(KeyPair keyPair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String publicKeyAddress = keyPair.getPublicKeyAddress();
        String hash = AccountTransactionRequest.generateHash(keyPair.getPublicKeyAddress(), currency, transactionOutputs);
        byte[] signature = Signing.sign(keyPair, hash);
        return new AccountTransactionRequest(publicKeyAddress, currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }

}

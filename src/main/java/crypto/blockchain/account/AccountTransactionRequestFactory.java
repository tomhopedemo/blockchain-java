package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountTransactionRequestFactory {

    public static Optional<AccountTransactionRequest> createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue, String id) throws ChainException {
        AccountCache accountBalanceCache = Data.getAccountBalanceCache(id);
        Long balance = accountBalanceCache.get(wallet.getPublicKeyAddress());
        if (balance < transactionValue) {
            return Optional.empty();
        }
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        AccountTransactionRequest transactionRequest = new AccountTransactionRequest(wallet.getPublicKeyAddress(), transactionOutputs);
        byte[] signature = calculateSignature(transactionRequest, wallet);
        transactionRequest.setSignature(signature);
        return Optional.of(transactionRequest);
    }

    public static byte[] calculateSignature(AccountTransactionRequest transactionRequest, Wallet wallet) throws ChainException {
        String transactionOutputsHash = transactionRequest.generateTransactionOutputsHash();
        byte[] preSignature = transactionOutputsHash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new ChainException(e);
        }
    }

}

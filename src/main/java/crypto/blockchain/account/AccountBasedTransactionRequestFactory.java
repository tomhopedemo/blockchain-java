package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.api.BlockchainData;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionRequestFactory {

    public static Optional<AccountTransactionRequest> createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue, String id) throws BlockchainException {
        AccountBalanceCache accountBalanceCache = BlockchainData.getAccountBalanceCache(id);
        Long balance = accountBalanceCache.get(wallet.getPublicKeyAddress());
        if (balance < transactionValue) {
            return Optional.empty();
        }
        List<AccountTransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new AccountTransactionOutput(recipientPublicKeyAddress, transactionValue));
        AccountTransactionRequest transactionRequest = new AccountTransactionRequest(wallet.publicKeyAddress, transactionOutputs);
        byte[] signature = calculateSignature(transactionRequest, wallet);
        transactionRequest.setSignature(signature);
        return Optional.of(transactionRequest);
    }

    public static byte[] calculateSignature(AccountTransactionRequest transactionRequest, Wallet wallet) throws BlockchainException{
        String transactionOutputsHash = transactionRequest.generateTransactionOutputsHash();
        byte[] preSignature = transactionOutputsHash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new BlockchainException(e);
        }
    }

}

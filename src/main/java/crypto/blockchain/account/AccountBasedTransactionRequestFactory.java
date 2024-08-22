package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionRequestFactory {

    public static Optional<AccountBasedTransactionRequest> createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue, AccountBalanceCache accountBalanceCache) throws BlockchainException {
        Long balance = accountBalanceCache.get(wallet.getPublicKeyAddress());
        if (balance < transactionValue) {
            return Optional.empty();
        }
        List<AccountBasedTransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new AccountBasedTransactionOutput(recipientPublicKeyAddress, transactionValue));
        AccountBasedTransactionRequest transactionRequest = new AccountBasedTransactionRequest(wallet.publicKeyAddress, transactionOutputs);
        byte[] signature = calculateSignature(transactionRequest, wallet);
        transactionRequest.setSignature(signature);
        return Optional.of(transactionRequest);
    }

    public static byte[] calculateSignature(AccountBasedTransactionRequest transactionRequest, Wallet wallet) throws BlockchainException{
        String transactionOutputsHash = transactionRequest.generateTransactionOutputsHash();
        byte[] preSignature = transactionOutputsHash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new BlockchainException(e);
        }
    }

    public static AccountBasedTransactionRequest genesisTransaction(Wallet genesis, long genesisTransactionValue)  {
        AccountBasedTransactionOutput genesisTransactionOutput = new AccountBasedTransactionOutput(genesis.getPublicKeyAddress(), genesisTransactionValue);
        return new AccountBasedTransactionRequest(null, List.of(genesisTransactionOutput));
    }

}

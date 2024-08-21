package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountBasedTransactionRequestFactory {

    WalletStore walletStore;
    AccountBalanceCache accountBalanceCache;

    public AccountBasedTransactionRequestFactory(WalletStore walletStore, AccountBalanceCache accountBalanceCache) {
        this.walletStore = walletStore;
        this.accountBalanceCache = accountBalanceCache;
    }

    public Optional<AccountBasedTransactionRequest> createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue) throws BlockchainException {
        Long balance = accountBalanceCache.get(wallet.publicKeyAddress);
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

    public byte[] calculateSignature(AccountBasedTransactionRequest transactionRequest, Wallet wallet) throws BlockchainException{
        String transactionOutputsHash = transactionRequest.generateTransactionOutputsHash();
        byte[] preSignature = transactionOutputsHash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.privateKey);
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new BlockchainException(e);
        }
    }

    public AccountBasedTransactionRequest genesisTransaction(Wallet walletA, long genesisTransactionValue) throws BlockchainException {
        AccountBasedTransactionOutput genesisTransactionOutput = new AccountBasedTransactionOutput(walletA.publicKeyAddress, genesisTransactionValue);
        List<AccountBasedTransactionOutput> transactionOutputs = List.of(genesisTransactionOutput);
        AccountBasedTransactionRequest genesisTransactionRequest = new AccountBasedTransactionRequest(null, transactionOutputs);
        accountBalanceCache.add(walletA.publicKeyAddress, genesisTransactionValue);
        return genesisTransactionRequest;
    }

}

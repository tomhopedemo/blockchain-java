package crypto.blockchain.service;

import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.utxo.UTXORequest;
import crypto.blockchain.utxo.UTXORequestFactory;

import java.util.Optional;

public class AuxService {

    public void addKey(String id, String publicKey, String privateKey) {
        Data.addWallet(id, new Wallet(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Request createRequest(BlockType type, String id, String from, String to, long value) throws BlockchainException {
        switch(type){
            case ACCOUNT -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    Optional<AccountTransactionRequest> transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(wallet.get(), to, value, id);
                    if (transactionRequest.isPresent()){
                        return transactionRequest.get();
                    }
                }
            }
            case UTXO -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    Optional<UTXORequest> utxoRequest = UTXORequestFactory.createUTXORequest(wallet.get(), to, value, id);
                    if (utxoRequest.isPresent()){
                        return utxoRequest.get();
                    }
                }
            }
        }
        return null;
    }


    public String createRequestJson(BlockType type, String id, String from, String to, long value) throws BlockchainException {
        switch(type){
            case ACCOUNT -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    Optional<AccountTransactionRequest> transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(wallet.get(), to, value, id);
                    if (transactionRequest.isPresent()){
                        return new GsonBuilder().create().toJson(transactionRequest.get());
                    }
                }
            }
            case UTXO -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    Optional<UTXORequest> utxoRequest = UTXORequestFactory.createUTXORequest(wallet.get(), to, value, id);
                    if (utxoRequest.isPresent()){
                        return new GsonBuilder().create().toJson(utxoRequest.get());
                    }
                }
            }
        }
        return null;
    }
}

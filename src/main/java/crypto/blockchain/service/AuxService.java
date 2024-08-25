package crypto.blockchain.service;

import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.utxo.TransactionCache;
import crypto.blockchain.utxo.TransactionRequest;
import crypto.blockchain.utxo.TransactionRequestFactory;

import java.util.Optional;

public class AuxService {

    public void addKey(String id, String publicKey, String privateKey) {
        Data.addWallet(id, new Wallet(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getBlockchain(id) != null;
    }

    public String createRequestJson(BlockType type, String id, String from, String to, long value) throws BlockchainException {
        switch(type){
            case ACCOUNT -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    Optional<AccountTransactionRequest> transactionRequest = AccountTransactionRequestFactory.createTransactionRequest(wallet.get(), to, value, id);
                    if (transactionRequest.isPresent()){
                        return new GsonBuilder().setPrettyPrinting().create().toJson(transactionRequest.get());
                    }
                }
            }
            case UTXO -> {
                Optional<Wallet> wallet = Data.getWallet(id, from);
                if (wallet.isPresent()) {
                    TransactionCache transactionCache = Data.getTransactionCache(id);
                    Optional<TransactionRequest> transactionRequest = TransactionRequestFactory.createTransactionRequest(wallet.get(), to, value, transactionCache);
                    if (transactionRequest.isPresent()){
                        return new GsonBuilder().setPrettyPrinting().create().toJson(transactionRequest.get());
                    }
                }
            }
        }
        return null;
    }
}

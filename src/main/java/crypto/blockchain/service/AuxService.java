package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.signed.SignedDataRequest;
import crypto.blockchain.signed.SignedDataRequestFactory;
import crypto.blockchain.utxo.UTXORequestFactory;

import java.util.List;
import java.util.Optional;


public class AuxService {

    static Gson JSON = new GsonBuilder().create();

    public void registerWallet(String id, String publicKey, String privateKey) {
        Data.addWallet(id, new Wallet(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Optional<? extends Request> createGenesisRequest(String id, BlockType type, String key, Object value) throws ChainException {
        Optional<Wallet> wallet = Data.getWallet(id, key);
        if (wallet.isEmpty()){
            return Optional.empty();
        }
        Request request = switch(type){
            case DATA -> new DataRequest((String) value);
            case SIGNED_DATA -> SignedDataRequestFactory.createSignedDataRequest(wallet.get(), (String) value).get();
            case ACCOUNT -> AccountTransactionRequest.create(wallet.get(), List.of(new TransactionOutput(key, (Long) value)));
            case UTXO -> UTXORequestFactory.createGenesisRequest(wallet.get().getPublicKeyAddress(), (Long) value, id);
        };
        return Optional.of(request);
    }

    public Optional<? extends Request> createRequest(BlockType type, String id, String from, String to, Object value) throws ChainException {
        Optional<Wallet> wallet = Data.getWallet(id, from);
        if (wallet.isEmpty()){
            return Optional.empty();
        }
        return switch(type){
            case DATA -> Optional.empty();
            case SIGNED_DATA -> SignedDataRequestFactory.createSignedDataRequest(wallet.get(), (String) value);
            case ACCOUNT -> AccountTransactionRequestFactory.createTransactionRequest(wallet.get(), to, (Long) value, id);
            case UTXO -> UTXORequestFactory.createUTXORequest(wallet.get(), to, (Long) value, id);
        };
    }

    public String createWalletJson(){
        return JSON.toJson(createWallet());
    }

    public Wallet createWallet() {
        return Wallet.generate();
    }

    public String createRequestJson(BlockType type, String id, String from, String to, long value) throws ChainException {
        Optional<? extends Request> request = createRequest(type, id, from, to, value);
        if (request.isEmpty()){
            return null;
        }
        return JSON.toJson(request.get(), type.getRequestClass());
    }

    public boolean validate(String id) {
        return ChainValidator.validate(id);
    }

}

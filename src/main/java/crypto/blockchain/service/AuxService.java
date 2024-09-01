package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.signed.SignedDataRequestFactory;
import crypto.blockchain.utxo.UTXORequestFactory;

import java.util.List;
import java.util.Optional;


public class AuxService {

    static Gson JSON = new GsonBuilder().create();

    public void registerKeyPair(String id, String publicKey, String privateKey) {
        Data.addKeyPair(id, new KeyPair(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Optional<? extends Request> createGenesisRequest(String id, BlockType type, String key, String currency, Object value) throws ChainException {
        Optional<KeyPair> KeyPair = Data.getKeyPair(id, key);
        if (KeyPair.isEmpty()){
            return Optional.empty();
        }
        Request request = switch(type){
            case DATA -> new DataRequest((String) value);
            case SIGNED_DATA -> SignedDataRequestFactory.createSignedDataRequest(KeyPair.get(), (String) value).get();
            case CURRENCY -> throw new UnsupportedOperationException();
            case ACCOUNT -> AccountTransactionRequestFactory.create(KeyPair.get(), currency, List.of(new TransactionOutput(key, (Long) value)));
            case UTXO -> UTXORequestFactory.createGenesisRequest(KeyPair.get().getPublicKeyAddress(), (Long) value, id);
        };
        return Optional.of(request);
    }

    public Optional<? extends Request> createRequest(BlockType type, String id, String from, String currency, String to, Object value) throws ChainException {
        Optional<KeyPair> KeyPair = Data.getKeyPair(id, from);
        if (KeyPair.isEmpty()){
            return Optional.empty();
        }
        return switch(type){
            case DATA -> Optional.empty();
            case SIGNED_DATA -> SignedDataRequestFactory.createSignedDataRequest(KeyPair.get(), (String) value);
            case CURRENCY -> throw new UnsupportedOperationException();
            case ACCOUNT -> AccountTransactionRequestFactory.createTransactionRequest(KeyPair.get(), currency, to, (Long) value, id);
            case UTXO -> UTXORequestFactory.createUTXORequest(KeyPair.get(), to, (Long) value, id);
        };
    }

    public String createKeyPairJson(){
        return JSON.toJson(createKeyPair());
    }

    public KeyPair createKeyPair() {
        return KeyPair.generate();
    }

    public String createRequestJson(BlockType type, String id, String from, String currency, String to, long value) throws ChainException {
        Optional<? extends Request> request = createRequest(type, id, from, currency, to, value);
        if (request.isEmpty()){
            return null;
        }
        return JSON.toJson(request.get(), type.getRequestClass());
    }

    public boolean validate(String id) {
        return ChainValidator.validate(id);
    }

}

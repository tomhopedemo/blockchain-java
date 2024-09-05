package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequestFactory;
import crypto.blockchain.api.data.TransactionalRequestParams;
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

    public Optional<? extends Request> createGenesisRequest(String id, BlockType type, String publicKey, String currency, Object value) throws ChainException {
        Optional<KeyPair> keyPair = Data.getKeyPair(id, publicKey);
        if (keyPair.isEmpty()){
            return Optional.empty();
        }
        Request request = switch(type){
            case ACCOUNT -> AccountTransactionRequestFactory.createGenesis(id, currency, List.of(new TransactionOutput(publicKey, (Long) value)));
            case UTXO -> UTXORequestFactory.createGenesisRequest(keyPair.get().getPublicKeyAddress(), (Long) value, id);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        return Optional.of(request);
    }

    public Optional<? extends Request> createDataRequest(BlockType type, String id, String key, String value) throws ChainException {
        Optional<KeyPair> KeyPair = Data.getKeyPair(id, key);
        if (KeyPair.isEmpty()){
            return Optional.empty();
        }
        return switch(type){
            case DATA -> Optional.of(new DataRequest(value));
            case SIGNED_DATA -> SignedDataRequestFactory.createSignedDataRequest(KeyPair.get(), value);
            case CURRENCY -> Optional.of(new CurrencyRequest(value, KeyPair.get().publicKeyAddress, KeyPair.get().privateKey));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Optional<? extends Request> createTransactionRequest(String id, TransactionalRequestParams transactionalRequestParams) throws ChainException {
        BlockType type = BlockType.valueOf(transactionalRequestParams.type());
        return switch(type){
            case ACCOUNT -> AccountTransactionRequestFactory.createTransactionRequest(id, transactionalRequestParams);
            case UTXO -> UTXORequestFactory.createUTXORequest(id, transactionalRequestParams);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public String createKeyPairJson(){
        return JSON.toJson(createKeyPair());
    }

    public KeyPair createKeyPair() {
        return KeyPair.generate();
    }

    public boolean validate(String id) {
        return ChainValidator.validate(id);
    }

}

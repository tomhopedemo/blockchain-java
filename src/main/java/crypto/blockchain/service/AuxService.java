package crypto.blockchain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.*;
import crypto.blockchain.account.AccountFactory;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.signed.SignedFactory;
import crypto.blockchain.utxo.UTXORequestFactory;

import java.util.Optional;


public class AuxService {

    static Gson JSON = new GsonBuilder().create();

    public void registerKeyPair(String id, String publicKey, String privateKey) {
        Data.addKeyPair(id, new KeyPair(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Request createGenesisRequest(String id, BlockType type, String publicKey, String currency, Object value) throws ChainException {
        Optional<KeyPair> keyPair = Data.getKeyPair(id, publicKey);
        if (keyPair.isEmpty()){
            return null;
        }
        return switch(type){
            case ACCOUNT -> {
                CurrencyRequest currencyRequest = Data.getCurrency(id, currency);
                if (currencyRequest == null){
                    yield null;
                }
                TransactionRequestParams params = new TransactionRequestParams.Builder()
                        .setCurrency(currency)
                        .setFrom(currencyRequest.publicKey())
                        .setTo(publicKey)
                        .setValue((Long)value)
                        .build();
                yield new AccountFactory(id).create(params);
            }
            case UTXO -> UTXORequestFactory.createGenesisRequest(keyPair.get().getPublicKeyAddress(), (Long) value, id);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Request createDataRequest(BlockType type, String id, String key, String value) throws ChainException {
        Optional<KeyPair> KeyPair = Data.getKeyPair(id, key);
        if (KeyPair.isEmpty()){
            return null;
        }
        return switch(type){
            case DATA -> new DataRequest(value);
            case SIGNED_DATA -> SignedFactory.createSignedDataRequest(KeyPair.get(), value);
            case CURRENCY -> new CurrencyRequest(value, KeyPair.get().publicKeyAddress, KeyPair.get().privateKey);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Request createTransactionRequest(String id, String type, TransactionRequestParams transactionRequestParams) throws ChainException {
        return switch(BlockType.valueOf(type)){
            case ACCOUNT -> new AccountFactory(id).create(transactionRequestParams);
            case UTXO -> UTXORequestFactory.createUTXORequest(id, transactionRequestParams);
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

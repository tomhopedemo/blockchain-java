package crypto.blockchain.service;

import crypto.blockchain.*;
import crypto.block.account.AccountFactory;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedFactory;
import crypto.block.utxo.UTXORequestFactory;

import java.util.List;


public class AuxService {

    public void registerKeyPair(String publicKey, String privateKey) {
        Data.addKeyPair(null, new KeyPair(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Request genesisRequest(String id, BlockType type, String publicKey, String currency, Object value) throws ChainException {
        KeyPair keyPair = Data.getKeyPair(id, publicKey);
        if (keyPair == null) return null;
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
            case UTXO -> UTXORequestFactory.createGenesisRequest(keyPair.publicKey(), (Long) value);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Request simple(String value) {
        return new DataRequest(value);
    }

    public Request signed(String id, String key, String value) throws ChainException {
        KeyPair keyPair = Data.getKeyPair(id, key);
        if (keyPair == null) return null;
        return SignedFactory.createSignedDataRequest(keyPair, value);
    }

    public Request currency(String id, String key, String value) throws ChainException {
        KeyPair keyPair = Data.getKeyPair(id, key);
        if (keyPair == null) return null;
        return new CurrencyRequest(value, keyPair.publicKey());
    }

    public Request account(String id, TransactionRequestParams transactionRequestParams) throws ChainException {
        return new AccountFactory(id).create(transactionRequestParams);
    }

    public Request utxo(String id, TransactionRequestParams transactionRequestParams) throws ChainException {
        return UTXORequestFactory.createUTXORequest(id, transactionRequestParams);
    }

    public KeyPair keypair() {
        return KeyPair.generate();
    }

    public boolean validate(String id) {
        return ChainValidator.validate(id);
    }

    public String key(String id) {
        List<String> keys = Data.getKeys(id);
        if (keys.isEmpty()) return null;
        return keys.getFirst();
    }
}

package crypto.blockchain.service;

import crypto.blockchain.*;
import crypto.block.account.AccountFactory;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedFactory;
import crypto.block.utxo.UTXORequestFactory;

import java.util.List;


public class AuxService {

    public void registerKeypair(Keypair keypair) {
        Data.addKeypair(null, keypair);
    }

    public void registerKeypair(String publicKey, String privateKey) {
        registerKeypair(new Keypair(privateKey, publicKey));
    }

    public boolean exists(String id){
        return Data.getChain(id) != null;
    }

    public Request genesisRequest(String id, BlockType type, String publicKey, String currency, Object value) throws ChainException {
        Keypair keypair = Data.getKeypair(id, publicKey);
        if (keypair == null) return null;
        return switch(type){
            case ACCOUNT -> {
                CurrencyRequest currencyRequest = Data.getCurrency(id, currency);
                if (currencyRequest == null) yield null;
                yield new AccountFactory(id).create(currencyRequest.publicKey(), publicKey, currency, (Long) value);
            }
            case UTXO -> UTXORequestFactory.createGenesisRequest(keypair.publicKey(), (Long) value);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Request simple(String value) {
        return new DataRequest(value);
    }

    public Request signed(String id, String key, String value) throws ChainException {
        Keypair keypair = Data.getKeypair(id, key);
        if (keypair == null) return null;
        return SignedFactory.createSignedDataRequest(keypair, value);
    }

    public Request currency(String id, String key, String value) throws ChainException {
        Keypair keypair = Data.getKeypair(id, key);
        if (keypair == null) return null;
        return new CurrencyRequest(value, keypair.publicKey());
    }

    public Request account(String id, String from, String to, String currency, Long value) throws ChainException {
        return new AccountFactory(id).create(from, to, currency, value);
    }

    public Request utxo(String id, String from, String to, String currency, Long value) throws ChainException {
        return UTXORequestFactory.createUTXORequest(id, from, to, currency, value);
    }

    public Keypair keypair() {
        return Keypair.generate();
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

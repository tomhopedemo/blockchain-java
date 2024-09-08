package crypto.service;

import crypto.*;
import crypto.block.account.AccountFactory;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedFactory;
import crypto.block.utxo.UTXORequestFactory;

import java.util.List;


public record AuxService(String id) {

    public void registerKeypair(Keypair keypair) {
        Data.addKeypair(null, keypair);
    }

    public void registerKeypair(String publicKey, String privateKey) {
        registerKeypair(new Keypair(privateKey, publicKey));
    }

    public boolean exists(){
        return Data.getChain(id) != null;
    }

    public Request genesisRequest(BlockType type, String publicKey, String currency, Object value) throws ChainException {
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

    public Request data(String value) {
        return new DataRequest(value);
    }

    public Request signed(String key, String value) throws ChainException {
        Keypair keypair = Data.getKeypair(id, key);
        if (keypair == null) return null;
        return SignedFactory.createSignedDataRequest(keypair, value);
    }

    public Request currency(String key, String value) throws ChainException {
        Keypair keypair = Data.getKeypair(id, key);
        if (keypair == null) return null;
        return new CurrencyRequest(value, keypair.publicKey());
    }

    public Request account(String from, String to, String currency, Long value) throws ChainException {
        return new AccountFactory(id).create(from, to, currency, value);
    }

    public Request utxo(String from, String to, String currency, Long value) throws ChainException {
        return UTXORequestFactory.createUTXORequest(id, from, to, currency, value);
    }

    public Keypair keypair() {
        return Keypair.generate();
    }

    public boolean validate() {
        return ChainValidator.validate(id);
    }

    public String key() {
        List<String> keys = Data.getKeys(id);
        if (keys.isEmpty()) return null;
        return keys.getFirst();
    }
}

package crypto.service;

import crypto.*;
import crypto.block.*;
import crypto.block.utxo.UTXORequestFactory;

import java.util.List;


public record AuxService(String id) {

    public void registerKeypair(Keypair keypair) {
        AuxData.addKeypair(keypair);
    }

    public void registerKeypair(String publicKey, String privateKey) {
        registerKeypair(new Keypair(privateKey, publicKey));
    }

    public boolean exists(){
        return Caches.getChain(id) != null;
    }

    public Request utxoGenesis(String publicKey, String currency, Object value) {
        Keypair keypair = Caches.getKeypair(id, publicKey);
        if (keypair == null) return null;
        return UTXORequestFactory.genesis(keypair.publicKey(), currency, (Long) value);
    }

    public Request account(String from, String to, String currency, Long value) throws ChainException {
        return Account.create(id, from, to, currency, value);
    }

    public Request currency(String key, String value) throws ChainException {
        Keypair keypair = Caches.getKeypair(id, key);
        if (keypair == null) return null;
        return new Currency(value, keypair.publicKey());
    }

    public Request data(String value) {
        return new Data(value);
    }

    public Keypair keypair() {
        return Keypair.create();
    }

    public Request signed(String key, String value) throws ChainException {
        Keypair keypair = Caches.getKeypair(id, key);
        if (keypair == null) return null;
        return Signed.create(keypair, value);
    }

    public Request stake(Keypair keypair, String currency) throws ChainException {
        return Stake.create(keypair, currency);
    }

    public Request utxo(String from, String to, String currency, Long value) throws ChainException {
        return UTXORequestFactory.create(id, from, to, currency, value);
    }

    public boolean validate() {
        return ChainValidator.validate(id);
    }

    public String key() {
        List<String> keys = Caches.getKeys(id);
        if (keys.isEmpty()) return null;
        return keys.getFirst();
    }
}

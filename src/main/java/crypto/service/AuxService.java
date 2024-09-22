package crypto.service;

import crypto.*;
import crypto.block.*;
import crypto.hashing.Hashing;

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
        return UTXO.genesis(keypair.publicKey(), currency, (Long) value);
    }

    public Request account(String from, String to, String currency, Long value) throws ChainException {
        return Transaction.create(id, from, to, currency, value);
    }

    public Request currency(String key, String value) throws ChainException {
        Keypair keypair = Caches.getKeypair(id, key);
        if (keypair == null) return null;
        return Currency.create(id, keypair, value);
    }

    public Keypair keypair() {
        return Keypair.create();
    }

    public Request data(String key, byte[] data, String format) throws ChainException {
        Keypair keypair = Caches.getKeypair(id, key);
        if (keypair == null) return null;
        return Data.create(id, keypair, data, format);
    }

    public Request branch(Keypair keypair, String branchKey) throws ChainException {
        return Branch.create(id, keypair, branchKey);
    }

    public Request merge(Keypair keypair, String branchKey) throws ChainException {
        return Merge.create(id, keypair, branchKey);
    }


    public Request stake(Keypair keypair, String currency) throws ChainException {
        return Stake.create(id, keypair, currency);
    }

    public Request hash(Keypair keypair, String hashType) throws ChainException {
        return Hash.create(id, keypair, Hashing.Type.valueOf(hashType));
    }

    public Request utxo(String from, String to, String currency, Long value) throws ChainException {
        return UTXO.create(id, from, to, currency, value);
    }

    public boolean validate() {
        return ChainValidator.validate(id);
    }

    public String key() {
        List<String> keys = Caches.getKeys(id);
        if (keys.isEmpty()) return null;
        return keys.getFirst();
    }

    public Create create(Keypair keypair) throws ChainException {
        return Create.create(id, keypair);
    }

    public Request publish(Keypair keypair) throws ChainException {
        return Publish.create(id, keypair);
    }

    public Stock stock(Keypair keypair, String stockKey, long update) throws ChainException {
        return Stock.create(id, keypair, stockKey, update);
    }
}

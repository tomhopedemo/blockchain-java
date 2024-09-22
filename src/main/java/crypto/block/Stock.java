package crypto.block;

import crypto.BlockData;
import crypto.Caches;
import crypto.ChainException;
import crypto.SimpleRequest;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

public record Stock(String key, String stockKey, String update) implements SimpleRequest<Stock> {

    @Override
    public void mine(String id, BlockData<Stock> b) {

    }

    @Override
    public String getPreHash() {
        return key + "~" + stockKey + "~" + update;
    }

    public static Stock create(String id, Keypair keypair, String stockKey, long update) throws ChainException {
        String hash = generateHash(keypair.publicKey(), stockKey, update, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Stock(keypair.publicKey(), stockKey, Encoder.encodeToHexadecimal(signature));
    }

    public static String generateHash(String key, String stockKey, long update, Hashing.Type hashType) {
        String preHash = key + "~" + stockKey + "~" + update;
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

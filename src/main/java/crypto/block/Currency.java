package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

public record Currency(String currency, String key, String signature) implements SimpleRequest<Currency> {

    @Override
    public void mine(String id, BlockData<Currency> blockData) {
        for (Currency request : blockData.data()) {
            if (!Caches.hasKey(id, request.key())) return;
            if (Caches.hasCurrency(id, request.currency())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Caches.addCurrency(id, request));
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {return signature;}

    public static Currency create(String id, Keypair keypair, String currency) throws ChainException {
        String hash = generateHash(keypair.publicKey(), currency, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Currency(currency, keypair.publicKey(), Encoder.encodeToHexadecimal(signature));
    }

    public static String generateHash(String key, String currency, Hashing.Type hashType) {
        String preHash = key + "~" + currency;
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }

}

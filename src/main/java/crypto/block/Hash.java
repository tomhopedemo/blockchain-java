package crypto.block;


import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

//verification that the key is the key/keys which own the chain - i guess
public record Hash(String key, String signature, String protocol) implements SimpleRequest<Hash> {

    @Override
    public void mine(String id, BlockData<Hash> blockData) {
        addBlock(id, blockData);

        blockData.data().forEach(request -> Caches.setHashType(id, Hashing.Type.valueOf(request.protocol)));
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return signature;
    }

    public static Hash create(String id, Keypair keypair, Hashing.Type hashType) throws ChainException {
        String hash = generateHash(keypair.publicKey(), hashType, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Hash(keypair.publicKey(), Encoder.encodeToHexadecimal(signature), hashType.toString());
    }

    public static String generateHash(String key, Hashing.Type value, Hashing.Type currentHashType) {
        String preHash = key + "~" + value;
        byte[] hash = Hashing.hash(preHash, currentHashType);
        return Encoder.encodeToHexadecimal(hash);
    }

}

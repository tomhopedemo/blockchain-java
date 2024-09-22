package crypto.block;


import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

//proof of creating the blockchain -
public record Create(String key, String signature) implements SimpleRequest<Create> {

    @Override
    public void mine(String id, BlockData<Create> blockData) {
        addBlock(id, blockData);
        Create first = blockData.data().getFirst();
        Caches.create(id, first.key());
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return signature;
    }

    public static Create create(String id, Keypair keypair) throws ChainException {
        String hash = generateHash(keypair.publicKey(), Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Create(keypair.publicKey(), Encoder.encodeToHexadecimal(signature));
    }

    //add in nonce
    public static String generateHash(String key, Hashing.Type hashType) {
        String preHash = key + "~" + Create.class.getName();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

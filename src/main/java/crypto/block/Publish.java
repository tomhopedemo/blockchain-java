package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

//this could be generalized to a flag mechanism. //also will require signature.
//cleaner mechansim for blocks with a single instance.
public record Publish(String key, String signature) implements SimpleRequest<Publish> {

    @Override
    public void mine(String id, BlockData<Publish> blockData) {
        if (blockData.data().size() != 1) return;
        if (Caches.isPublished(id)) return;
        Publish first = blockData.data().getFirst();
        if (!verify(id, first)) return;
        addBlock(id, blockData);
        Caches.publish(id);
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return signature;
    }

    public static Publish create(String id, Keypair keypair) throws ChainException {
        String hash = generateHash(keypair.publicKey(), Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Publish(keypair.publicKey(), Encoder.encodeToHexadecimal(signature));
    }

    //add in nonce
    public static String generateHash(String key, Hashing.Type hashType) {
        String preHash = key + "~" + Create.class.getName();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

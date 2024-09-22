package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

import java.util.List;

public record Referendum(String key, String referendumKey, String question, List<String> options, boolean allowFreeform, String signature) implements SimpleRequest<Referendum> {
    @Override
    public void mine(String id, BlockData<Referendum> blockData) {
        addBlock(id, blockData);
        blockData.data().forEach(request -> Caches.addReferendum(id, request));
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return signature;
    }

    public static Referendum create(String id, Keypair keypair, String referendumKey, String name, List<String> options, boolean allowFreeform) throws ChainException {
        String hash = generateHash(keypair.publicKey(), referendumKey, name, options, allowFreeform, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Referendum(keypair.publicKey(), referendumKey, name, options, allowFreeform, Encoder.encodeToHexadecimal(signature));
    }

    public static String generateHash(String key, String referendumKey, String name, List<String> options, boolean allowFreeform, Hashing.Type hashType) {
        String preHash = key + "~" + referendumKey + "~" + name + "~" + String.join("@", options) + "~" + allowFreeform + "~" + Branch.class.getName();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

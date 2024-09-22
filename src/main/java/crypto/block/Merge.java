package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;

public record Merge(String publicKey, String branchKey, String signature) implements SimpleRequest<Merge> {
    @Override
    public void mine(String id, BlockData<Merge> blockData) {
        addBlock(id, blockData);
        //checke that the public key is the owner of the branch that is being merged
        blockData.data().forEach(request -> Caches.removeBranch(id, request.branchKey));
        //not sure if i need to simply remove the branch here - prbably if it's a merge.
        //the owner of the merged chain may allow in the future for the entities on the chain
        //to request later their funds from any combined account which were not brought back over to the main chain.
        //likely with cost of doing business on main chain involved
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return signature;
    }

    public static Branch create(String id, Keypair keypair, String branchId) throws ChainException {
        String hash = generateHash(keypair.publicKey(), branchId, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Branch(keypair.publicKey(), branchId, Encoder.encodeToHexadecimal(signature));
    }

    ////////
    //considering it is the same key and branchId - i think there needs to be something else to sign here.
    //ig uess the type - so maybe we need a nonce here ( and in general maybe we need a nonce )
    public static String generateHash(String key, String branchId, Hashing.Type hashType) {
        String preHash = key + "~" + branchId + "~" + Merge.class.getName();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;


//alternatively - we create the branch and then allow the currencies to be registered on branch.
//the registration would involve creating the currency on main branch - which triggers
//request to create on the subbranch.
//funds are moved by a similar mechanism a transaction which puts a hold on the funds mvoed to the subbranch.
//note that if we're merging from the branch to main branch then we either have to
//
//alternate switches to different version of the currency, and the chain merges back and certain
//points. e.g. once every 100 blocks. responsibility for merging would have to be
//performed by consensus.

//alternately again we can perform a collateralized mechanism, whereby the main chain
//only really knows of a single account which it interactives with. so as an individual
//i send to a collateral account, and when i pull money out of the side chain,
//it is expected that there will be a transaction back to my existing acct.

//more likely the idea of a side chain is to facilitate faster payments, so likely
//wouldn't be super distributed.

//what would registering a currency to branch look like - the branch would be pv.keyed.
//and registering currency on that branch would be a mechanism by same key.
//when you want to move ccy -> branch you move to an account on-chain.
//the side chain creates currency after x number of blocks say.
//while on the side chain, is there any certainty/connexion between your
//original currency. i.e. can the work done on the side chain be
//audited to confirm that it is generally correct.

//can you give a star rating to a side chain.
//

//longest chain wins
//


//and bridges to other chains are another aspect to consider / as well as off ramps / on ramps.

//i think creating the branch under a key/sinature is probably the best thing to do
//and then we can allow the branch to be used variably.
//there would not be operations directly from one branch to the other
//rather we would rely on an intermediary to connect the two toegether.

//how do we know which hashing mechanism to use for generating requests now -
//when the hashing mechanism needs to be changed, will there be a lot of notice,
//are there restrictions on how it is changed to prevent issues with creating requests
//maybe we can say that once the chain is 'published' the hashing mechanism is fixed.

//however i think it's better to allow it to be modified - it would be changed by the owner
//

public record Branch(String key, String branchKey, String signature) implements SimpleRequest<Branch> {

    @Override
    public void mine(String id, BlockData<Branch> blockData) {
        addBlock(id, blockData);
        blockData.data().forEach(request -> Caches.addBranch(id, request));
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

    public static String generateHash(String key, String branchId, Hashing.Type hashType) {
        String preHash = key + "~" + branchId + "~" + Branch.class.getName();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }
}

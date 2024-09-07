package crypto.blockchain.signed;

import crypto.blockchain.*;
import crypto.blockchain.account.AccountRequest;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record SignedFactory(String id) implements BlockFactory<SignedRequest>{

    @Override
    public void mine(BlockData<SignedRequest> requests) {
        //Data Request Verification
        for (SignedRequest signedRequest : requests.data()) {
            boolean verified = verify(signedRequest);
            if (!verified) {
                return;
            }
        }

        //Create block
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);

        //Update caches
        Requests.remove(id, requests.data(), BlockType.SIGNED_DATA);
    }

    @Override
    public BlockData<SignedRequest> prepare(List<SignedRequest> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(SignedRequest request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKeyAddress());
            String hash = SignedRequest.generateHash(request.publicKeyAddress(), request.value());
            return ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()));
        } catch (GeneralSecurityException e){
            return false;
        }
    }

    public static SignedRequest createSignedDataRequest(KeyPair keyPair, String value) throws ChainException {
        String hash = SignedRequest.generateHash(keyPair.getPublicKeyAddress(), value);
        byte[] signature = Signing.sign(keyPair, hash);
        return new SignedRequest(keyPair.getPublicKeyAddress(), value, Encoder.encodeToHexadecimal(signature));
    }

}

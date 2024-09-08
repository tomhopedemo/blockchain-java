package crypto.block.signed;

import crypto.blockchain.*;
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
    public void mine(BlockData<SignedRequest> blockData) {
        if (!verify(blockData)) return;
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), BlockType.SIGNED_DATA);
    }

    @Override
    public BlockData<SignedRequest> prepare(List<SignedRequest> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(SignedRequest request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = SignedRequest.generateHash(request.publicKey(), request.value());
            return ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()));
        } catch (GeneralSecurityException e){
            return false;
        }
    }

    public static SignedRequest createSignedDataRequest(Keypair keypair, String value) throws ChainException {
        String hash = SignedRequest.generateHash(keypair.publicKey(), value);
        byte[] signature = Signing.sign(keypair, hash);
        return new SignedRequest(keypair.publicKey(), value, Encoder.encodeToHexadecimal(signature));
    }

}

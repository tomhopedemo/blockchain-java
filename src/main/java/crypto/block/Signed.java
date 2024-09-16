package crypto.block;

import crypto.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


public record Signed(String publicKey, String value, String signature) implements Request<Signed> {

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String publicKey, String value) {
        String preHash = publicKey + "~" + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    @Override
    public void mine(String id, BlockData<Signed> blockData) {
        if (!verify(id, blockData)) return;
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), BlockType.SIGNED);
    }

    @Override
    public BlockData<Signed> prepare(String id, List<Signed> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(String id, Signed request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = Signed.generateHash(request.publicKey(), request.value());
            return ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()));
        } catch (GeneralSecurityException e){
            return false;
        }
    }

    public static Signed create(Keypair keypair, String value) throws ChainException {
        String hash = Signed.generateHash(keypair.publicKey(), value);
        byte[] signature = Signing.sign(keypair, hash);
        return new Signed(keypair.publicKey(), value, Encoder.encodeToHexadecimal(signature));
    }

}

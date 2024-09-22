package crypto.block;

import crypto.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record Data(String key, String value, String format, String signature) implements Request<Data> {

    public Data(String key, byte[] data, String format, String signature) { this(key, new String(data, UTF_8), format, signature);}

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String key, String value, String format, Hashing.Type hashType) {
        String preHash = key + "~" + value + "~" + format;
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }

    @Override
    public void mine(String id, BlockData<Data> blockData) {
        if (!verify(id, blockData)) return;
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public BlockData<Data> prepare(String id, List<Data> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(String id, Data request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.key());
            String hash = Data.generateHash(request.key(), request.value(), format, Caches.getHashType(id));
            return ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()));
        } catch (GeneralSecurityException e){
            return false;
        }
    }

    public static Data create(String id, Keypair keypair, byte[] data, String format) throws ChainException {
        String hash = Data.generateHash(keypair.publicKey(), new String(data, UTF_8), format, Caches.getHashType(id));
        byte[] signature = Signing.sign(keypair, hash);
        return new Data(keypair.publicKey(), data, format, Encoder.encodeToHexadecimal(signature));
    }

}

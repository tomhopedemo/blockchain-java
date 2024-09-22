package crypto.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Hashing {

    public enum Type {
        SHA_256,
        SHA3_256
    }

    public static byte[] hash(String preHash, Type hashType) {
        try {
            return switch (hashType){
                case SHA_256  -> MessageDigest.getInstance("SHA-256").digest(MessageDigest.getInstance("SHA-256").digest(preHash.getBytes(UTF_8)));
                case SHA3_256 -> MessageDigest.getInstance("SHA3-256").digest(preHash.getBytes(UTF_8));
                case null     -> throw new NoSuchAlgorithmException();
            };
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

}

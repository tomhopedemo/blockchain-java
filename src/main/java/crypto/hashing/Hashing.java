package crypto.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Hashing {


    public static byte[] hash(String preHash) {
        try {
//            return MessageDigest.getInstance("SHA-256").digest(MessageDigest.getInstance("SHA-256").digest(preHash.getBytes(UTF_8)));
            return MessageDigest.getInstance("SHA3-256").digest(preHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

}

package demo.hashing;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Hashing {

    public static byte[] hash(String preHash) throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(preHash.getBytes(UTF_8));
    }

}

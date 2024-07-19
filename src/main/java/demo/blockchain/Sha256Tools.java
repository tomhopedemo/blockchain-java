package demo.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Sha256Tools {
    public static String applySha256HexadecimalEncoding(String input) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexadecimalEncoding = new StringBuilder();
        for (byte b : hash) {
            String byteHexadecimalEncoding = Integer.toHexString(b);
            if (byteHexadecimalEncoding.length() == 1) hexadecimalEncoding.append('0');
            hexadecimalEncoding.append(byteHexadecimalEncoding);
        }
        return hexadecimalEncoding.toString();
    }
}

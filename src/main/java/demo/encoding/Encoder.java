package demo.encoding;

import java.security.Key;
import java.util.Base64;

public class Encoder {

    public static String encode(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String encodeToHexadecimal(byte[] byteArray){
        StringBuilder hexadecimalEncoding = new StringBuilder();
        for (byte b : byteArray) {
            hexadecimalEncoding.append(String.format("%02x", b));
        }
        return hexadecimalEncoding.toString();
    }
//
//    public static String encodeToHexadecimalOld(byte[] hash) {
//        StringBuilder hexadecimalEncoding = new StringBuilder();
//        for (byte b : hash) {
//            String byteHexadecimalEncoding = Integer.toHexString(b);
//            if (byteHexadecimalEncoding.length() == 1) hexadecimalEncoding.append('0');
//            hexadecimalEncoding.append(byteHexadecimalEncoding);
//        }
//        return hexadecimalEncoding.toString();
//    }


}

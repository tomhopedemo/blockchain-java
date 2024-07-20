package demo.encoding;

import org.bouncycastle.util.encoders.Hex;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encoder {

    public static String encodeToString(Key key) {
        byte[] encodedKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    public static PublicKey decodeToPublicKey(String encodedAsString) throws Exception {
        byte[] encodedAsByteArray = Base64.getDecoder().decode(encodedAsString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedAsByteArray);
        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
        return factory.generatePublic(spec);
    }

    public static PrivateKey decodeToPrivateKey(String encodedAsString) throws Exception {
        byte[] encodedAsByteArray = Base64.getDecoder().decode(encodedAsString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedAsByteArray);
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        return kf.generatePrivate(spec);
    }

    public static String encodeToHexadecimal(byte[] byteArray){
        StringBuilder hexadecimalEncoding = new StringBuilder();
        for (byte b : byteArray) {
            hexadecimalEncoding.append(String.format("%02x", b));
        }
        return hexadecimalEncoding.toString();
    }

}

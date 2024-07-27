package demo.encoding;

import org.bouncycastle.util.encoders.Hex;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encoder {

    public static String encodeToString(Key key) {
        byte[] encodedKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    public static PublicKey decodeToPublicKey(String encodedAsString) throws GeneralSecurityException {
        byte[] encodedAsByteArray = Base64.getDecoder().decode(encodedAsString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedAsByteArray);
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance("ECDSA", "BC");
        } catch (GeneralSecurityException e){
            throw new RuntimeException();
        }
        return factory.generatePublic(spec);
    }

    public static PrivateKey decodeToPrivateKey(String encodedAsString) throws InvalidKeySpecException {
        byte[] encodedAsByteArray = Base64.getDecoder().decode(encodedAsString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedAsByteArray);
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("ECDSA", "BC");
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
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

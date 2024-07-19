package demo.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class ECDSA {

    public static byte[] calculateECDSASignature(PrivateKey privateKey, String input) throws Exception {
        Signature signature = Signature.getInstance("ECDSA", "BC");
        signature.initSign(privateKey);
        signature.update(input.getBytes());
        return signature.sign();
    }

    public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}

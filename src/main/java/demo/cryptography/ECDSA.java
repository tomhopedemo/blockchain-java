package demo.cryptography;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ECDSA {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
        keyPairGenerator.initialize(new ECGenParameterSpec("prime192v1"), SecureRandom.getInstance("SHA1PRNG"));
        return keyPairGenerator.generateKeyPair();
    }

    //used to unlock a transaction output.
    public static byte[] calculateECDSASignature(PrivateKey privateKey, byte[] signatureInput) throws Exception {
        Signature ecdsa = Signature.getInstance("ECDSA", "BC");
        ecdsa.initSign(privateKey);
        ecdsa.update(signatureInput);
        return ecdsa.sign();
    }

    //used by blockchain nodes to verify that the signature provided to unlock a transaction output is valid
    public static boolean verifyECDSASignature(PublicKey publicKey, byte[] signatureInput, byte[] signature) throws Exception{
        Signature ecdsa = Signature.getInstance("ECDSA", "BC");
        ecdsa.initVerify(publicKey);
        ecdsa.update(signatureInput);
        return ecdsa.verify(signature);
    }
}

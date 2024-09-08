package crypto.cryptography;

import crypto.blockchain.Keypair;
import crypto.encoding.Encoder;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ECDSA {

    public static KeyPair generateKeyPair()  {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            keyPairGenerator.initialize(new ECGenParameterSpec("prime192v1"), SecureRandom.getInstance("SHA1PRNG"));
            return keyPairGenerator.generateKeyPair();
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPair keyPair = ECDSA.generateKeyPair();

        byte[] encrypt = encrypt(keyPair.getPublic(), "hellowhat".getBytes(StandardCharsets.UTF_8));
        byte[] decrypt = decrypt(keyPair.getPrivate(), encrypt);
        System.out.println(new String(decrypt));
    }

    public static byte[] encrypt(PublicKey key, byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(PrivateKey key, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }

    public static boolean checkKeypair(Keypair keypair) {
        byte[] signatureInput = "anyString".getBytes(StandardCharsets.UTF_8);
        try {
            byte[] signature = calculateECDSASignature(Encoder.decodeToPrivateKey(keypair.privateKey()), signatureInput);
            return verifyECDSASignature(Encoder.decodeToPublicKey(keypair.publicKey()), signatureInput, signature);
        } catch (Exception e){
            return false;
        }
    }

    //used to unlock a transaction output.
    public static byte[] calculateECDSASignature(PrivateKey privateKey, byte[] signatureInput) throws GeneralSecurityException {
        Signature ecdsa;
        try {
            ecdsa = Signature.getInstance("ECDSA", "BC");
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
        ecdsa.initSign(privateKey);
        ecdsa.update(signatureInput);
        return ecdsa.sign();
    }

    //used by blockchain nodes to verify that the signature provided to unlock a transaction output is valid
    public static boolean verifyECDSASignature(PublicKey publicKey, byte[] signatureInput, byte[] signature) throws GeneralSecurityException {
        Signature ecdsa;
        try {
            ecdsa = Signature.getInstance("ECDSA", "BC");
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
        ecdsa.initVerify(publicKey);
        ecdsa.update(signatureInput);
        return ecdsa.verify(signature);
    }

}

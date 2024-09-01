package crypto.blockchain;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.*;

public class KeyPair {
    public String privateKey;
    public String publicKeyAddress;

    private KeyPair(PrivateKey privateKey, PublicKey publicKeyAddress) {
        this.privateKey = Encoder.encodeToString(privateKey);
        this.publicKeyAddress = Encoder.encodeToString(publicKeyAddress);
    }

    public KeyPair(String privateKey, String publicKeyAddress){
        this.privateKey = privateKey;
        this.publicKeyAddress = publicKeyAddress;
    }

    public static KeyPair generate() {
        java.security.KeyPair keyPair = ECDSA.generateKeyPair();
        return new KeyPair(keyPair.getPrivate(), keyPair.getPublic());
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyAddress() {
        return publicKeyAddress;
    }
}

package crypto.blockchain;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.*;

public record KeyPair (String privateKey, String publicKey) implements Request {

    private KeyPair(PrivateKey privateKey, PublicKey publicKey) {
        this(Encoder.encodeToString(privateKey), Encoder.encodeToString(publicKey));
    }

    public static KeyPair generate() {
        java.security.KeyPair keyPair = ECDSA.generateKeyPair();
        return new KeyPair(keyPair.getPrivate(), keyPair.getPublic());
    }

    @Override
    public String getPreHash() {
        return privateKey + "~" + publicKey;
    }
}

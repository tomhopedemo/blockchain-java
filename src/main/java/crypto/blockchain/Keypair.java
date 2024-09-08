package crypto.blockchain;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.*;

public record Keypair(String privateKey, String publicKey) implements Request {

    private Keypair(PrivateKey privateKey, PublicKey publicKey) {
        this(Encoder.encodeToString(privateKey), Encoder.encodeToString(publicKey));
    }

    public static Keypair generate() {
        KeyPair keyPair = ECDSA.generateKeyPair();
        return new Keypair(keyPair.getPrivate(), keyPair.getPublic());
    }

    @Override
    public String getPreHash() {
        return privateKey + "~" + publicKey;
    }
}

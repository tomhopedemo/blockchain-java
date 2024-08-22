package crypto.blockchain;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.*;

public class Wallet {
    public String privateKey;
    public String publicKeyAddress;

    private Wallet(PrivateKey privateKey, PublicKey publicKeyAddress) {
        this.privateKey = Encoder.encodeToString(privateKey);
        this.publicKeyAddress = Encoder.encodeToString(publicKeyAddress);
    }

    public static Wallet generate() {
        KeyPair keyPair = ECDSA.generateKeyPair();
        return new Wallet(keyPair.getPrivate(), keyPair.getPublic());
    }

}

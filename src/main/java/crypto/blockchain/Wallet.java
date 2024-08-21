package crypto.blockchain;

import crypto.encoding.Encoder;

import java.security.*;

public class Wallet {
    public String privateKey;
    public String publicKeyAddress;

    public Wallet(PrivateKey privateKey, PublicKey publicKeyAddress) {
        this.privateKey = Encoder.encodeToString(privateKey);
        this.publicKeyAddress = Encoder.encodeToString(publicKeyAddress);
    }

}

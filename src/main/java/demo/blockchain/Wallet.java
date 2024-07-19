package demo.blockchain;

import java.security.*;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKeyAddress;

    public Wallet(PrivateKey privateKey, PublicKey publicKeyAddress) {
        this.privateKey = privateKey;
        this.publicKeyAddress = publicKeyAddress;
    }

}

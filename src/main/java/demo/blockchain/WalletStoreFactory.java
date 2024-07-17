package demo.blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

public class WalletStoreFactory {

    int numWallets;
    public WalletStoreFactory(int numWallets) {
        this.numWallets = numWallets;
    }

    public WalletStore generate() {
        WalletStore walletStore = new WalletStore();
        try {
            for (int i = 0; i < numWallets; i++) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
                keyPairGenerator.initialize(new ECGenParameterSpec("prime192v1"), SecureRandom.getInstance("SHA1PRNG"));
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                Wallet wallet = new Wallet(keyPair.getPrivate(), keyPair.getPublic());
                walletStore.add(wallet);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return walletStore;
    }
}

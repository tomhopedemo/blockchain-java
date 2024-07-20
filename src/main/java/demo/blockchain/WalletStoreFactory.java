package demo.blockchain;

import demo.cryptography.ECDSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

public class WalletStoreFactory {

    int numWallets;
    public WalletStoreFactory(int numWallets) {
        this.numWallets = numWallets;
    }

    public WalletStore generate() throws Exception {
        WalletStore walletStore = new WalletStore();
        for (int i = 0; i < numWallets; i++) {
            KeyPair keyPair = ECDSA.generateKeyPair();
            Wallet wallet = new Wallet(keyPair.getPrivate(), keyPair.getPublic());
            walletStore.add(wallet);
        }
        return walletStore;
    }
}

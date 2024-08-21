package crypto.blockchain;

import crypto.cryptography.ECDSA;

import java.security.KeyPair;

public class WalletStoreFactory {

    int numWallets;
    public WalletStoreFactory(int numWallets) {
        this.numWallets = numWallets;
    }

    public WalletStore generate() {
        WalletStore walletStore = new WalletStore();
        for (int i = 0; i < numWallets; i++) {
            KeyPair keyPair = ECDSA.generateKeyPair();
            Wallet wallet = new Wallet(keyPair.getPrivate(), keyPair.getPublic());
            walletStore.add(wallet);
        }
        return walletStore;
    }
}

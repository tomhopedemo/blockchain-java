package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Base64;
import java.util.List;

public class SuperWalletVisualiser {

    WalletStore walletStore;

    public SuperWalletVisualiser(WalletStore walletStore) {
        this.walletStore = walletStore;
    }

    public void visualise(){
        List<Wallet> wallets = walletStore.get();
        for (Wallet wallet : wallets) {
            String privateKeyJson = Base64.getEncoder().encodeToString(wallet.privateKey.getEncoded());
            String publicKeyJson = Base64.getEncoder().encodeToString(wallet.publicKeyAddress.getEncoded());
            System.out.println(privateKeyJson);
            System.out.println(publicKeyJson);
        }
    }
}

package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import demo.encoding.Encoder;

import java.util.List;
import java.util.stream.Collectors;

public class SuperWalletVisualiser {

    WalletStore walletStore;

    public SuperWalletVisualiser(WalletStore walletStore) {
        this.walletStore = walletStore;
    }

    public void visualise(){
        List<Wallet> wallets = walletStore.get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(wallets);
        System.out.println(json);
    }
}

package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class WalletStore {

    List<Wallet> wallets = new ArrayList<>();

    public WalletStore() {
    }

    public void add(Wallet blockchain){
        wallets.add(blockchain);
    }

    public Wallet get(int index){
        return wallets.get(index);
    }

}

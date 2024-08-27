package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WalletCache {

    private final List<Wallet> wallets = new ArrayList<>();

    public Optional<Wallet> getWallet(String publicKey){
        return wallets.stream().filter(w -> publicKey.equals(w.getPublicKeyAddress())).findAny();
    }

    public List<Wallet> getWallets(){
        return this.wallets;
    }

    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }

}

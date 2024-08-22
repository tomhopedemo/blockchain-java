package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class WalletCache {

    private final List<Wallet> wallets;

    private final Wallet genesisWallet;

    public WalletCache(Wallet genesis) {
        this.wallets = new ArrayList<>();
        this.wallets.add(genesis);
        this.genesisWallet = genesis;
    }

    public Wallet getGenesisWallet(){
        return genesisWallet;
    }

    public List<Wallet> getWallets(){
        return this.wallets;
    }

    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }
}

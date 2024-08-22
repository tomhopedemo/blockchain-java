package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class WalletCache {

    private final List<Wallet> wallets = new ArrayList<>();

    private Wallet genesisWallet;

    public Wallet getGenesisWallet(){
        return genesisWallet;
    }

    public List<Wallet> getWallets(){
        return this.wallets;
    }

    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }

    public void addGenesisWallet(Wallet genesis) {
        genesisWallet = genesis;
        wallets.add(genesis);
    }
}

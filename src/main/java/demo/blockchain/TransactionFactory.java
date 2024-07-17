package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;

public class TransactionFactory {

    Blockchain blockchain;
    WalletStore walletStore;
    TransactionCache transactionCache = new TransactionCache();

    public TransactionFactory(Blockchain blockchain, WalletStore walletStore) {
        this.blockchain = blockchain;
        this.walletStore = walletStore;
    }



}

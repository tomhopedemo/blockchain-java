package crypto.blockchain;

import crypto.blockchain.api.Data;
import crypto.blockchain.utxo.*;

public class ComboBlockchain {

    public static void create(String id){
        Data.addBlockchain(new Blockchain(id));
        Data.addTransactionCache(id);
        Data.addAccountBalanceCache(id);
        Data.addWalletCache(id);
    }


    //multiple genesis accounts - and so we're no longer going to maintain genesis.
    //the generation of genesis account will require a genesis key to be sent/will return the key.
    public static void genesis(String id, long value) throws BlockchainException {
        Wallet genesis = Wallet.generate();
        Data.addGenesisWallet(id, genesis);

        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.genesisTransaction(genesis, value, transactionCache);
        TransactionBlockchain.mineNextBlock(transactionRequest, id, 1);
    }


}

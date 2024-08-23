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

    public static void genesis(String id, long value, String genesisKey) throws BlockchainException {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest transactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        TransactionBlockchain.mineNextBlock(transactionRequest, id, 1);
    }

}

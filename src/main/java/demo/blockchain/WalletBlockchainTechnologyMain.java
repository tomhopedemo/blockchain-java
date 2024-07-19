package demo.blockchain;

import java.security.Security;

public class WalletBlockchainTechnologyMain {

    private record RunParameters (int difficulty, int numBlockchains, int numBlocksToMine) { }

    public static void main(String[] args) throws Exception {
        RunParameters runParameters = new RunParameters(1, 1, 0);
        long genesisTransactionValue = 100;

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(3).generate();
        TransactionCache transactionCache = new TransactionCache();

        Wallet walletGenesis = walletStore.get(0);
        Wallet walletA = walletStore.get(1);
        Wallet walletB = walletStore.get(2);

        //Transacting
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(walletStore, transactionCache);
        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletGenesis, walletA, genesisTransactionValue);
        TransactionRequest transactionRequest = transactionRequestFactory.sendFunds(walletA, walletB.publicKeyAddress, 5);

        //Mining
        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, runParameters.difficulty());
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);
        transactionBlockMining.mineNextBlock(transactionRequest);

        //Validation
        BlockchainStore blockchainStore = new BlockchainStore();
        blockchainStore.add(blockchain);
        SuperValidator superValidator = new SuperValidator(blockchainStore);
        superValidator.validate();

        //Visualization
        new SuperBlockchainVisualiser(blockchainStore).visualise();
        new SuperTransactionOutputVisualizer(transactionCache).visualise();
        new SuperWalletVisualiser(walletStore).visualise();
    }
}

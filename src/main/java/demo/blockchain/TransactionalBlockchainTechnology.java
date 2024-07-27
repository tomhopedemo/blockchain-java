package demo.blockchain;

import java.security.Security;

public class TransactionalBlockchainTechnology {

    public TransactionalBlockchainTechnology() {
    }

    public void execute(int difficulty, long genesisTransactionValue) throws BlockchainException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        Blockchain blockchain = new Blockchain("0");
        WalletStore walletStore = new WalletStoreFactory(2).generate();
        Wallet walletA = walletStore.get(0);
        Wallet walletB = walletStore.get(1);

        TransactionCache transactionCache = new TransactionCache();
        TransactionBlockMining transactionBlockMining = new TransactionBlockMining(blockchain, difficulty, transactionCache);
        TransactionRequestFactory transactionRequestFactory = new TransactionRequestFactory(walletStore, transactionCache);

        //Mining
        TransactionRequest genesisTransactionRequest = transactionRequestFactory.genesisTransaction(walletA, genesisTransactionValue);
        transactionBlockMining.mineNextBlock(genesisTransactionRequest);

        TransactionRequest transactionRequest = transactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, 5).get();
        transactionBlockMining.mineNextBlock(transactionRequest);


        //Validation
        BlockchainStore blockchainStore = new BlockchainStore();
        blockchainStore.add(blockchain);
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Serialisation
        BlockchainSerialisation blockchainSerialisation = new BlockchainSerialisation();
        boolean stable = blockchainSerialisation.checkSerializationStable(blockchain);
        if (!stable){
            throw new BlockchainException("Unstable Blockchain Serialization");
        }

        //Visualization
        if (Control.VISUALIZE_IN_CONSOLE) {
            Visualiser visualiser = new Visualiser();
            visualiser.visualise(blockchain);
            visualiser.visualise(transactionCache);
            visualiser.visualise(walletStore);
        }
        System.out.println("Complete.");
    }
}

package demo.blockchain;

import demo.encoding.Encoder;
import demo.objects.Block;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Serialisation
        String serialisedBlockchain = new BlockchainSerialiser(blockchain).serialise();
        Blockchain deserialisedBlockchain = new BlockchainDeserialiser(serialisedBlockchain).deserialise();

        //Visualization
        new BlockchainVisualiser(blockchain).visualise();
        new BlockchainVisualiser(deserialisedBlockchain).visualise();
        new SuperBlockchainVisualiser(blockchainStore).visualise();
        new SuperTransactionOutputVisualiser(transactionCache).visualise();
        new SuperWalletVisualiser(walletStore).visualise();
    }

    List<TransactionInput> reconstructTransactionInputs(String inputTransactionDatas){
        List<TransactionInput> transactionInputs = new ArrayList<>();
        String[] inputTransactionSerialised = inputTransactionDatas.split(" ");
        for (String line : inputTransactionSerialised) {
            String[] split = line.split("\\?"); //probably would be best for this to just be jsonified.
            transactionInputs.add(new TransactionInput(split[0], split[1].getBytes(UTF_8)));
        }
        return transactionInputs;
    }
}

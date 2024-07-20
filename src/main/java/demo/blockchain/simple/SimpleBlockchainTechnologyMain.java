package demo.blockchain.simple;

import demo.blockchain.*;

import java.security.Security;

public class SimpleBlockchainTechnologyMain {

    private record RunParameters (int difficulty, int numBlockchains, int numBlocksToMine) { }

    public static void main(String[] args) throws Exception {
        RunParameters runParameters = new RunParameters(1, 2, 4);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        SimpleBlockchainSuperFactory simpleBlockchainSuperFactory = new SimpleBlockchainSuperFactory(runParameters.difficulty(), runParameters.numBlockchains());
        BlockchainStore blockchainStore = simpleBlockchainSuperFactory.construct();

        //Mining
        SimpleSuperBlockMining simpleSuperBlockMining = new SimpleSuperBlockMining(runParameters.numBlocksToMine(), runParameters.difficulty());
        simpleSuperBlockMining.mine(blockchainStore);

        //Validation
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Visualization
        SuperBlockchainVisualiser superBlockchainVisualiser = new SuperBlockchainVisualiser(blockchainStore);
        superBlockchainVisualiser.visualise();
    }

}

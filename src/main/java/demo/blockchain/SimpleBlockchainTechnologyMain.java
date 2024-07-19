package demo.blockchain;

import java.security.Security;

public class SimpleBlockchainTechnologyMain {

    private record RunParameters (int difficulty, int numBlockchains, int numBlocksToMine) { }

    public static void main(String[] args) throws Exception {
        RunParameters runParameters = new RunParameters(1, 2, 4);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        BlockchainSuperFactory blockchainSuperFactory = new BlockchainSuperFactory(runParameters.difficulty(), runParameters.numBlockchains());
        BlockchainStore blockchainStore = blockchainSuperFactory.construct();

        //Mining
        SuperBlockMining superBlockMining = new SuperBlockMining(runParameters.numBlocksToMine(), runParameters.difficulty());
        superBlockMining.mine(blockchainStore);

        //Validation
        SuperValidator superValidator = new SuperValidator(blockchainStore);
        superValidator.validate();

        //Visualization
        SuperBlockchainVisualiser superBlockchainVisualiser = new SuperBlockchainVisualiser(blockchainStore);
        superBlockchainVisualiser.visualise();
    }

}

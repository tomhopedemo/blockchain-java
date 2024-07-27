package demo.blockchain.simple;

import demo.blockchain.*;

import java.security.Security;

public class SimpleBlockchainTechnology {

    public void execute(int difficulty, int numBlockchains, int numBlocksToMine) {
        //Construction
        SimpleBlockchainSuperFactory simpleBlockchainSuperFactory = new SimpleBlockchainSuperFactory(difficulty, numBlockchains);
        BlockchainStore blockchainStore = simpleBlockchainSuperFactory.construct();

        //Mining
        SimpleSuperBlockMining simpleSuperBlockMining = new SimpleSuperBlockMining(numBlocksToMine, difficulty);
        simpleSuperBlockMining.mine(blockchainStore);

        //Validation
        SuperBlockchainValidator superBlockchainValidator = new SuperBlockchainValidator(blockchainStore);
        superBlockchainValidator.validate();

        //Visualization
        if (Control.VISUALIZE_IN_CONSOLE) {
             new Visualiser().visualise(blockchainStore);
        }

        System.out.println("Complete.");
    }

}

package demo.blockchain.simple;

import demo.blockchain.*;

public class SimpleBlockchainTechnology {

    public void execute(String id, int difficulty, int numBlocksToMine) {

        SimpleBlockchainFactory simpleBlockchainFactory = new SimpleBlockchainFactory(difficulty);
        Blockchain blockchain = simpleBlockchainFactory.createBlockchainWithGenesisBlock(id);

        //Mining
        SimpleBlockMining simpleBlockMining = new SimpleBlockMining(difficulty);
        for (int i = 0; i < numBlocksToMine; i++) {
            Block nextBlock = simpleBlockMining.mineNextBlock(blockchain);
            blockchain.add(nextBlock);
        }

        //Validation
        BlockchainValidator blockchainValidator = new BlockchainValidator();
        blockchainValidator.validate(blockchain);


        //Visualization
        if (Control.VISUALIZE_IN_CONSOLE) {
             new Visualiser().visualise(blockchain);
        }

        System.out.println("Complete.");
    }

}

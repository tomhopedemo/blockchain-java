package demo.blockchain;

import demo.blockchain.simple.SimpleBlockchainTechnology;

import java.util.HashMap;
import java.util.Map;

public class Control {
    final static boolean VISUALIZE_IN_CONSOLE = true;
    static String executionBlock = "b";

    static Map<String, ExecutionControl> blocks = createExecutionBlocks();

    static Map<String, ExecutionControl> createExecutionBlocks() {
        Map<String, ExecutionControl> blocks = new HashMap<>();
        blocks.put("m", new MultiX(3, 100L));
        blocks.put("s", new SingleX(3, 100L));
        blocks.put("b", new BlockX(3, 2, 5));
        return blocks;
    }

    public static void main(String[] args) throws Exception {
        blocks.get(executionBlock).execute();
        System.exit(0);
    }


    static public class BlockX implements ExecutionControl {

        int difficulty;
        int numBlockchains;
        int numBlocksToMine;

        public BlockX(int difficulty, int numBlockchains, int numBlocksToMine) {
            this.difficulty = difficulty;
            this.numBlockchains = numBlockchains;
            this.numBlocksToMine = numBlocksToMine;
        }

        @Override
        public void execute() throws Exception {
            new SimpleBlockchainTechnology().execute(difficulty, numBlockchains, numBlocksToMine);
        }
    }


    static public class SingleX implements ExecutionControl {

        int difficulty;
        long genesisTransactionValue;

        public SingleX(int difficulty, long genesisTransactionValue) {
            this.difficulty = difficulty;
            this.genesisTransactionValue = genesisTransactionValue;
        }

        @Override
        public void execute() throws Exception {
            new TransactionalBlockchainTechnology().execute(difficulty, genesisTransactionValue);
        }
    }

    static public class MultiX implements ExecutionControl {

        int difficulty;
        long genesisTransactionValue;

        public MultiX(int difficulty, long genesisTransactionValue) {
            this.difficulty = difficulty;
            this.genesisTransactionValue = genesisTransactionValue;
        }

        @Override
        public void execute() throws Exception {
            new MultiTransactionalBlockchainTechnology().execute(difficulty, genesisTransactionValue);
        }
    }

    public interface ExecutionControl {
        void execute() throws Exception;
    }


}
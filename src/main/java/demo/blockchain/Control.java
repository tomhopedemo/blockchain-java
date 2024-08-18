package demo.blockchain;

import demo.blockchain.account.AccountBasedBlockchainTechnology;
import demo.blockchain.simple.SimpleBlockchainTechnology;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;


/**
 *  1. multi version of account based
 */

public class Control {

    public final static boolean VISUALIZE_IN_CONSOLE = true;
    static String executionBlock = "a";
    static int difficulty = 1;
    static boolean RUN_ALL = true;

    static Map<String, ExecutionControl> executionControls = createExecutionBlocks();

    static Map<String, ExecutionControl> createExecutionBlocks() {
        Map<String, ExecutionControl> blocks = new HashMap<>();
        blocks.put("m", new MultiX(difficulty, 100L));
        blocks.put("s", new SingleX(difficulty, 100L));
        blocks.put("a", new AccountX(difficulty, 100L));
        blocks.put("b", new BlockX(difficulty, 2, 5));
        return blocks;
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if (RUN_ALL){
            for (ExecutionControl executionControl : executionControls.values()) {
                executionControl.execute();
            }
        } else {
            executionControls.get(executionBlock).execute();
        }
        System.exit(0);
    }


    public record BlockX  (int difficulty, int numBlockchains, int numBlocksToMine) implements ExecutionControl {
        public void execute() {
            new SimpleBlockchainTechnology().execute(difficulty, numBlockchains, numBlocksToMine);
        }
    }

    public record AccountX (int difficulty, long genesisTransactionValue) implements ExecutionControl {
        public void execute() throws Exception {
            new AccountBasedBlockchainTechnology().execute(difficulty, genesisTransactionValue);
        }
    }

    public record SingleX (int difficulty, long genesisTransactionValue) implements ExecutionControl {
        public void execute() throws Exception {
            new TransactionalBlockchainTechnology().execute(difficulty, genesisTransactionValue);
        }
    }

    public record MultiX (int difficulty, long genesisTransactionValue) implements ExecutionControl {
        public void execute() throws Exception {
            new MultiTransactionalBlockchainTechnology().execute(difficulty, genesisTransactionValue);
        }
    }

    public interface ExecutionControl {
        void execute() throws Exception;
    }


}
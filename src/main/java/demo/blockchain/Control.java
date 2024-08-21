package demo.blockchain;

import demo.blockchain.account.AccountBasedBlockchainTechnology;
import demo.blockchain.account.MultiAccountBasedBlockchainTechnology;
import demo.blockchain.simple.SimpleBlockchainTechnology;
import demo.blockchain.utxo.MultiTransactionalBlockchainTechnology;
import demo.blockchain.utxo.TransactionalBlockchainTechnology;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;


/**
 *  0. implement multiple types of object in same blockchain
 *  1. implement staking + leader identification
 *  2. accoutn bsed -> Overall Verification (account balance) - would want to check that either there is only one transaction per account or the account covers all
 *  3. visualization of blockchain data to be of the form, select block/blocks
 *  4. blockchain validation can happen at blockchain level
 */

public class Control {

    public static boolean VISUALIZE_IN_CONSOLE = true;
    static String executionBlock = "mab";
    static int difficulty = 1;
    static boolean RUN_ALL = false;

    static Map<String, ExecutionControl> executionControls = createExecutionBlocks();

    static Map<String, ExecutionControl> createExecutionBlocks() {
        Map<String, ExecutionControl> blocks = new HashMap<>();
        blocks.put("b", new BlockX(difficulty, 2, 5));
        blocks.put("s", new SingleX(difficulty, 100L));
        blocks.put("m", new MultiX(difficulty, 100L));
        blocks.put("a", new AccountX(difficulty, 100L));
        blocks.put("mab", new MultiAccountBasedX(difficulty, 100L));
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


    public record BlockX(int difficulty, int numBlockchains, int numBlocksToMine) implements ExecutionControl {
        public void execute() {
            new SimpleBlockchainTechnology().execute("test", difficulty, numBlocksToMine);
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

    public record MultiAccountBasedX (int difficulty, long genesisTransactionValue) implements ExecutionControl {
        public void execute() throws Exception {
            new MultiAccountBasedBlockchainTechnology().execute("mab", difficulty, genesisTransactionValue);
        }
    }

    public interface ExecutionControl {
        void execute() throws Exception;
    }


}
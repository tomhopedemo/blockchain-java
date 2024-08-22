package crypto.blockchain;

import crypto.blockchain.account.AccountBasedBlockchainTech;
import crypto.blockchain.account.MultiAccountBasedBlockchainTech;
import crypto.blockchain.api.BlockchainService;
import crypto.blockchain.api.BlockchainType;
import crypto.blockchain.simple.SimpleBlockchainTech;
import crypto.blockchain.utxo.MultiTransactionalBlockchainTech;
import crypto.blockchain.utxo.TransactionalBlockchainTech;

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
    static boolean RUN_ALL = false;
    static long genesisValue = 100L;
    static int difficulty = 1;
    static BlockchainType blockchainType = BlockchainType.SIMPLE;

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        if (RUN_ALL){
            for (BlockchainType value : BlockchainType.values()) {
                BlockchainService.createBlockchain(value.toString(), value, difficulty, genesisValue);
            }
        } else {
            Blockchain blockchain = BlockchainService.createBlockchain(blockchainType.toString(), blockchainType, difficulty, genesisValue);
            BlockchainValidator.validate(blockchain);
            if (VISUALIZE_IN_CONSOLE) {
                Visualiser.visualise(blockchain);
            }
        }
        System.exit(0);
    }

    public interface ExecutionControl {
        Blockchain execute() throws BlockchainException;
    }
}
package crypto.blockchain.api;

import crypto.blockchain.MinerPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
/**
 *  1. staking + leader identification (note i like the idea that nodes can be spun up at
 *  will, and we can have a list of leaders). also for staking should there be a limit.
 *  is it fair that you need a lot to pay for staking.
 *  traditional staking - so stake transaction. staking transaction.
 *  //this can either be to a specific account - which is dirty - or a different block type.
 *  //so we'll iterate through until we find a valid version. you pay to write the value
 *  //you'll be notified ahead of time - core node. will exist to always do this.
 *  2. visualization of blockchain data to be of the form, select block/blocks
 *  4. mining difficulty for utxo, want this to be programmable with algos. it shouldn't be a value. it should be a calc itself,potentially somethign which is set on chain and not specific to a block type. I think it should be set on the chain and accessed
 *  5. always have a node available for the 'client' - so store the chain.
 *
 */
@SpringBootApplication
public class Control {
    public static final String CORS = "http://localhost:3000";

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(Control.class, args);
    }

}

package crypto.blockchain.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
/**
 *  1. implement staking + leader identification
 *  2. accoutn bsed -> Overall Verification (account balance) - would want to check that either there is only one transaction per account or the account covers all
 *  3. visualization of blockchain data to be of the form, select block/blocks
 *  4. blockchain validation can happen at blockchain level
 *  5. hot wallet version //cold wallet version - i think the only difference is one is a local.
 *  6. documentation of the endpoints/endpoint driven design.
 *  7. we have x at home (companies which are concepts/sentences). companies which are not even companies
 *  8. we have x at home is have a go at building anything.
 *  9. rather than building different services, create instance of a monolith with certain features enabled.
 *  10. modify simulate controller to do all of the aspects, including holding the wallet locally. and creating the genesis blocks

 we need to solve this type issue. i don't want to be explictly passing around type and at the same
 time i dont' want to be dependent on type being specified at blockchain level that can't be changed
 later. so an alternative is that we retain it on the block level i.e. it is the block itself which is typed.
 so actually you can try and add any block type to the blockchain......
 and whether or not the blockchain is valid or not depends on the software which checks it.


 */
@SpringBootApplication
public class Control {
    public static final String CORS = "http://localhost:3000";

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(Control.class, args);
    }

}

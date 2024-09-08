package crypto.blockchain.api;

import crypto.blockchain.MinerPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
/**
 *  1. staking + leader identification (note i like the idea that nodes can be spun up at
 *  will, and we can have a list of leaders). also for staking should there be a limit.
 *  is it fair that you need a lot to pay for staking. - stake block
 *  traditional staking - so stake transaction. staking transaction.
 *  //this can either be to a specific account - which is dirty - or a different block type.
 *  //so we'll iterate through until we find a valid version. you pay to write the value
 *  //you'll be notified ahead of time - core node. will exist to always do this.
 *  2. visualization of blockchain data to be of the form, select block/blocks
 *  3. mining difficulty for utxo, want this to be programmable with algos. it shouldn't be a value. it should be a calc itself,potentially somethign which is set on chain and not specific to a block type. I think it should be set on the chain and accessed
 *  4. always have a node available for the 'client' - so store the chain.
 *  5. switch to make blockchain public e.g. once genesis created or when decided
 *      meaning we need a way to keep the blockchain private - there can be a
 *      publish button. this will also fix the 'version' of the software.
 *      i.e. you get a version of the software as well as the actual blockchain.
 *
 *      //alternatively each time you access the server,
 *      //a new instance of this software gets run
 *      //and so there is no worry about other blockchains.
 *      //there will also be a public version which is such that you can interract.
 *
 *  6. await visibility of data in chain
 *  7. voting/referendum blocks :) questions and answers
 *  8. coin minting
 *  9. side chains
 *  10. image data
 *  11. live questions and answers .e.g in a classroom
 *  12. stock implementation
 *  13. simulate -> generate sample
 *  14. option for lock on the block itself for a simple block
 *  15. parametrize number of blocks generated in simulation
 *  16. create transaction for simple
 *  17. change accounttransaction to account
 *  18. consider simplification of generateIntegratedHash
 *  19. option to choose hashing mechanism
 *  20. for account genesis indication either an explicit flag (requires extra data on object)?,
 *  or based on being the first block (doesn't work with our type or future extensions to type system), or same as utxo (would have no signature - there could be a public private key) - quite like the idea as an option, but maybe not default as rewquires more data
 *  why do we need a signature, is it okay for the signature to be empty?
 *  how can we indicate it's supposed to be genesis? - i think a public private key is a really good idea.
 *  so we'll need to include this somehow.
 *  21. keypair block (public key, private key) - we have currency
 *  22. big one: data associated with transaction to be encrypted via public key
 *  23. should accounttransactinorequest or accounttransactionrequests contiain knowledge of the coin - i think the next step is for the holder to contain info on coin. - public private key pair indicating that the coin is public for everyone.
 *  24. separator character in pre-hash for all hashing;
 *  25. consider moving hashing methods to a separate file
 *  26. simulate and genesis to actually create required blocks for specific currency
 *  27. in Data switch back to not creating objects on get
 *  28. proof of work where machines are given codes for performing actual work
 *  29. share code for block creation in blockfactory
 *  30. deserialiseRequest to use enum
 *  31. allow currencies for utxo chains too
 *  32. create object the same as currency request with a different name (like an empty wrapper)
 *  33. your wallet undefied types
 *  34. create next app rename title to blockchains
 *  35. add text to the account transaction to act as a nonce ( and don't allow a transaction to reoccur w/ same signature)
 *  36. add keypair to 'global' keypair cache
 *  37. global keypair cache details to be merged with specific chain details on get.
 *  38. ability to create own schema for block type
 *  39. transaction output convert to record
 *  40. simulate return types to be updated
 *  41. merge API classes
 *  42. at some point the AUX service needs to start being a separate instance from the mining rig
 *  43. log at 'api-debug' level failures at api
 *  44. switch to considering requests as objects ( which have been imbued with request interface )
 *  45. signedata -> signed
 *  46. simple -> data
 *  47. auxservice create null chain reference on startup.
 *  48 transactionrequestparams -> individual params
 *  50. get the type from the request object in submit request
 *  51. simulate reuse static fields for services
 *  52. for simulate currency, we'll need to also simulate the public key - this will be passed in.
 *  53. simulate methods in separate class and simulate api call in shared api class
 *  54. register key pair to operate on KeyPair object
 */


@SpringBootApplication
public class Control {
    public static final String CORS = "http://localhost:3000";

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        MinerPool.start();
        SpringApplication.run(Control.class, args);
    }

}

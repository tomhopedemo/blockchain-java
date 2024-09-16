package crypto.api;

import crypto.MinerPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
/**
 *  1. switch to make version of your blockchain public - public block? - signed using ownership key ( as below )
 *  2. await visibility of data in chain
 *  3. voting/referendum blocks :) questions and answers
 *  4. coin minting
 *  5. side chains
 *  6. image data
 *  7. live questions and answers .e.g in a classroom
 *  8. stock implementation
 *  9. simulate -> generate sample
 *  14. option for lock on the block itself for a simple block
 *  15. parametrize number of blocks generated in simulation
 *  16. create transaction for simple
 *  17. change accounttransaction to account
 *  18. consider simplification of generateIntegratedHash
 *  19. option to choose hashing mechanism
 *  20. for account genesis indication either an explicit flag (requires extra data on object)?, or based on being the first block (doesn't work with our type or future extensions to type system), or same as utxo (would have no signature - there could be a public private key) - quite like the idea as an option, but maybe not default as rewquires more data why do we need a signature, is it okay for the signature to be empty? how can we indicate it's supposed to be genesis? - i think a public private key is a really good idea. so we'll need to include this somehow.
 *  21. keypair block (public key, private key) - we have currency
 *  22. big one: data associated with transaction to be encrypted via public key
 *  23. should accounttransactinorequest or accounttransactionrequests contiain knowledge of the coin - i think the next step is for the holder to contain info on coin. - public private key pair indicating that the coin is public for everyone.
 *  24. separator character in pre-hash for all hashing;
 *  25. consider moving hashing methods to a separate file
 *  26. simulate and genesis to actually create required blocks for specific currency
 *  27. in Caches switch back to not creating objects on get
 *  28. proof of work where machines are given codes for performing actual work
 *  29. share code for block creation in blockfactory
 *  30. deserialiseRequest to use enum
 *  32. create object the same as currency request with a different name (like an empty wrapper)
 *  35. add text to the account transaction to act as a nonce ( and don't allow a transaction to reoccur w/ same signature)
 *  36. add keypair to 'global' keypair cache
 *  37. global keypair cache details to be merged with specific chain details on get?
 *  38. ability to create own schema for block type...
 *  40. simulate return types to be updated
 *  41. at some point the AUX service needs to start being a separate instance from the mining rig
 *  42. log at 'api-debug' level failures at api
 *  43. switch to considering requests as objects ( which have been imbued with request interface )
 *  44. auxservice create null chain reference on startup?
 *  45. refactor genesis switch
 *  46. review genesis endpoint
 *  47. replace ecdsa main method with an encrypt/decrypt impl
 *  48. transactionoutput to have single constructor
 *  49. generateTransactionOutputHash to use similar hashing mechanism to request
 *  50. utxo include check that all inputs/outputs have same currency.
 *  51. usage for encrypt/decrypt (e.g. upload encrypted data)
 *  52. utxo with similar genesis as account from existing.
 *  53. leader identification (note i like the idea that nodes can be spun up at will, and we can have a list of leaders). also for staking should there be a limit. is it fair that you need a lot to pay for staking. - stake block traditional staking - so stake transaction. staking transaction. //this can either be to a specific account - which is dirty - or a different block type.//so we'll iterate through until we find a valid version. you pay to write the val//you'll be notified ahead of time - core node. will exist to always do this.
 *  54. cache package
 *  55. currency x caches - review if possible to share some of the fucntionality amont them.
 *  56. typeFactory - standard mechanism to prepare, consisting of looping and verifying.
 *  57. generalize Requests even further.
 *  58. add factory to type constructor
 *  59. remove sample chain
 *  60. add stake and keypair to the webapp.
 *  61. registered keyparis to be held in a separate cache.
 *  62. change favicon to a C in a circle ( from favicon generator website )
 *  63. reconsider whether keys should be saved on creation in aux service ( which is private anyway ).
  * 64. all rules/code added to chain at start.
 *  65. read and use latest difficulty.
 *  66. add staking to the BlockFactory
 *  67. review required use of nonce in Block hash (the index position should be usable, and perhaps automatically)
 *  68. ownership (of chain) block - single public key- referenced by e.g. switch ( generalizable to multiple owners? )
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

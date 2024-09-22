package crypto.api;

import crypto.MinerPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
/**
 *  big one: data associated with transaction to be encrypted via public key
 *  should accounttransactinorequest or accounttransactionrequests contiain knowledge of the coin - i think the next step is for the holder to contain info on coin. - public private key pair indicating that the coin is public for everyone.
 *  separator character in pre-hash for all hashing;
 *  consider moving hashing methods to a separate file
 *  simulate and genesis to actually create required blocks for specific currency
 *  in Caches switch back to not creating objects on get
 *  proof of work where machines are given codes for performing actual work
 *  share code for block creation in blockfactory
 *  deserialiseRequest to use enum
 *  create object the same as currency request with a different name (like an empty wrapper)
 *  add text to the account transaction to act as a nonce ( and don't allow a transaction to reoccur w/ same signature)
 *  add keypair to 'global' keypair cache
 *  global keypair cache details to be merged with specific chain details on get?
 *  ability to create own schema for block type...
 *  simulate return types to be updated
 *  at some point the AUX service needs to start being a separate instance from the mining rig
 *  log at 'api-debug' level failures at api
 *  switch to considering requests as objects ( which have been imbued with request interface )
 *  auxservice create null chain reference on startup?
 *  refactor genesis switch
 *  review genesis endpoint
 *  replace ecdsa main method with an encrypt/decrypt impl
 *  transactionoutput to have single constructor
 *  generateTransactionOutputHash to use similar hashing mechanism to request
 *  utxo include check that all inputs/outputs have same currency.
 *  usage for encrypt/decrypt (e.g. upload encrypted data)
 *  utxo with similar genesis as account from existing.
 *  leader identification (note i like the idea that nodes can be spun up at will, and we can have a list of leaders). also for staking should there be a limit. is it fair that you need a lot to pay for staking. - stake block traditional staking - so stake transaction. staking transaction. //this can either be to a specific account - which is dirty - or a different block type.//so we'll iterate through until we find a valid version. you pay to write the val//you'll be notified ahead of time - core node. will exist to always do this.
 *  cache package
 *  currency x caches - review if possible to share some of the fucntionality amont them.
 *  typeFactory - standard mechanism to prepare, consisting of looping and verifying.
 *  generalize Requests even further.
 *  add factory to type constructor
 *  remove sample chain
 *  add stake and keypair to the webapp.
 *  registered keyparis to be held in a separate cache.
 *  change favicon to a C in a circle ( from favicon generator website )
 *  reconsider whether keys should be saved on creation in aux service ( which is private anyway ).
  * all rules/code added to chain at start.
 *  read and use latest difficulty.
 *  add staking to the BlockFactory
 *  review required use of nonce as field in Block hash (the index position should be usable for nonce, and perhaps automatically)
 *  ownership (of chain) block - single public key- referenced by e.g. switch ( generalizable to multiple owners? )
 *  simulate publish
 *  pull prehash method in request to a different shared place, identify what is common with their construction and get fields reflexively in order.
 *  generalize code in mine method in request if possible.
 *  decide on making code private again. although i'm already associated with it. the voting is a different aspect.
 *  inclusion of v.v. hard to crack keys for v. important operations e.g. subchains/ref. chains.
 *  fixed number of validators - as e.g. trusted institutinos like banks.
 *  attempt integration with stock managment
 *  attempt intoegration with species presentation.
 *  load blocks from files
 *  visualize data without keys/with key mappings.
 *  key mapping/alias
 *  rename Transaction to transaction.
 *  modify the prehash delimiter mechanism, likely switching to an index based approach.
 *  live questions and answers simulation
 *  allow transactionality i.e. put a lock resources in the block. the lock is posted.
 *  alt, can say based on the data in this block. + high priority.
 *  ownership transaction for signed?
 *  consider renaming blockdatahashable to hashable
 *  many of the request objects will require signatures added also - identify which. then a refactor will be prudent as signature logic shared amongst many
 *  add more structure to crytpo package
 *  review utxo
 *  when creating the block, i think we might need to identify which block type is being represented.
 *  bridges
 *  fiat off ramps/on ramps
 *  generalize caches
 *  modify calculateHash in Block to an index based approach
 *  review whether blockdatahashable/blockdata can be merged
 *  make project private
 *  requests remove (switch around parameters)
 *  change public key to key (except for keypair/and encryption protocols)
 *  execute before 'block'
 *  time/date onramp/integration
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

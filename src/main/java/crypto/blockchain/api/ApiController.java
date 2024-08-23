package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.BlockchainValidator;
import crypto.blockchain.Wallet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
/**
 *  0. implement multiple types of object in same blockchain
 *  1. implement staking + leader identification
 *  2. accoutn bsed -> Overall Verification (account balance) - would want to check that either there is only one transaction per account or the account covers all
 *  3. visualization of blockchain data to be of the form, select block/blocks
 *  4. blockchain validation can happen at blockchain level
 */
@SpringBootApplication
@RestController
public class ApiController {

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(ApiController.class, args);
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/combo/{id}")
    String combo(@PathVariable("id") String id) {
        try {
            BlockchainType type = BlockchainType.COMBO;
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain == null) {
                blockchain = BlockchainService.createBlockchain(id, type);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/wallet")
    String wallet() {
        Wallet wallet = Wallet.generate();
        return new GsonBuilder().create().toJson(wallet);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/create/{id}")
    String create(@PathVariable("id") String id) {
        try {
            BlockchainType type = BlockchainType.MULTI_ACCOUNT;
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain == null) {
                blockchain = BlockchainService.createBlockchain(id, type);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/genesis/{id}")
    String genesis(@PathVariable("id") String id, @RequestParam("publicKey") String publicKey) {
        try {
            BlockchainType type = BlockchainType.MULTI_ACCOUNT;
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain != null) {
                blockchain = BlockchainService.createGenesisBlock(id, type, 100L, publicKey);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/simulate/{id}")
    String simulate(@PathVariable("id") String id) {
        try {
            BlockchainType type = BlockchainType.MULTI_ACCOUNT;
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain != null) {
                Wallet from = Data.getGenesisWallet(id);
                blockchain = BlockchainService.simulateBlocks(type, id, 1, 1, from);
                BlockchainValidator.validate(blockchain);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

}

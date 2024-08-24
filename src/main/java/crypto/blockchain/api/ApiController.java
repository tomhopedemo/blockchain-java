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
 *  1. implement staking + leader identification
 *  2. accoutn bsed -> Overall Verification (account balance) - would want to check that either there is only one transaction per account or the account covers all
 *  3. visualization of blockchain data to be of the form, select block/blocks
 *  4. blockchain validation can happen at blockchain level
 */
@SpringBootApplication @RestController
public class ApiController {
    static final String CORS = "http://localhost:3000";

    @GetMapping("/wallet/create") @CrossOrigin(origins = CORS)
    String wallet() {
        Wallet wallet = Wallet.generate();
        return new GsonBuilder().create().toJson(wallet);
    }

    @GetMapping("/blockchain/create")  @CrossOrigin(origins = CORS)
    String create(@RequestParam("id") String id, @RequestParam("type") String type) {
        try {
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain == null) {
                blockchain = BlockchainService.createBlockchain(id, BlockchainType.valueOf(type));
                return new GsonBuilder().create().toJson(blockchain);
            } else {
                return null;
            }
        } catch (BlockchainException e){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/genesis/{id}")  @CrossOrigin(origins = CORS)
    String genesis(@RequestParam("id") String id,
                   @RequestParam("publicKey") String publicKey,
                   @RequestParam("type") String type,
                   @RequestParam("value") Long value
                   ) {
        try {
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain != null) {
                blockchain = BlockchainService.createGenesisBlock(id, BlockchainType.valueOf(type), value, publicKey);
                return new GsonBuilder().create().toJson(blockchain);
            } else {
                return null;
            }
        } catch (BlockchainException e){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/simulate/{id}")  @CrossOrigin(origins = CORS)
    String simulate(@RequestParam("id") String id,
                    @RequestParam("publicKey") String publicKey,
                    @RequestParam("type") String type,
                    @RequestParam("value") Long value){
        try {
            Blockchain blockchain = BlockchainService.getBlockchain(id);
            if (blockchain != null) {
                Wallet from = Data.getGenesisWallet(id);
                blockchain = BlockchainService.simulateBlock(BlockchainType.valueOf(type), id, from);
                BlockchainValidator.validate(blockchain);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(ApiController.class, args);
    }


}

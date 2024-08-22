package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.BlockchainValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

@SpringBootApplication
@RestController
public class ApiController {

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(ApiController.class, args);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/create/{id}")
    String create(@PathVariable("id") String id) {
        try {
            BlockchainType type = BlockchainType.MULTI_ACCOUNT;
            Blockchain blockchain = BlockchainService.getBlockchain(type, id);
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
    String genesis(@PathVariable("id") String id) {
        try {
            BlockchainType type = BlockchainType.MULTI_ACCOUNT;
            Blockchain blockchain = BlockchainService.getBlockchain(type, id);
            if (blockchain != null) {
                blockchain = BlockchainService.createGenesisBlock(id, type, 100L);
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
            Blockchain blockchain = BlockchainService.getBlockchain(type, id);
            if (blockchain != null) {
                blockchain = BlockchainService.simulateBlocks(type, id, 1, 1);
                BlockchainValidator.validate(blockchain);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }

}

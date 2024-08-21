package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

import static crypto.blockchain.Control.VISUALIZE_IN_CONSOLE;

@SpringBootApplication
@RestController
public class ApiController {

    public static void main(String... args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        VISUALIZE_IN_CONSOLE = false;
        SpringApplication.run(ApiController.class, args);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/blockchain/{id}")
    String blockchain(@PathVariable("id") String id) {
        Blockchain blockchain = BlockchainService.getBlockchain(id);
        if (blockchain == null){
            blockchain = BlockchainService.createBlockchain(id);
        }
        return new GsonBuilder().create().toJson(blockchain);
    }

}

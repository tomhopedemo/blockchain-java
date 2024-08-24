package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.BlockchainValidator;
import crypto.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.ApiApplication.CORS;

@RestController
public class BlockchainSimulateController {
    @Autowired BlockchainService blockchainService;

    @GetMapping("/simulate/{id}")  @CrossOrigin(origins = CORS)
    String simulate(@RequestParam("id") String id,
                    @RequestParam("publicKey") String publicKey,
                    @RequestParam("type") String type,
                    @RequestParam("value") Long value){
        try {
            Blockchain blockchain = blockchainService.getBlockchain(id);
            if (blockchain != null) {
                Wallet from = Data.getGenesisWallet(id);
                blockchain = blockchainService.simulateBlock(BlockchainType.valueOf(type), id, from);
                BlockchainValidator.validate(blockchain);
            }
            return new GsonBuilder().create().toJson(blockchain);
        } catch (BlockchainException e){
            return null;
        }
    }
}

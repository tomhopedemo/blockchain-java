package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.ApiApplication.CORS;

@RestController
public class BlockchainGenesisController {

    @Autowired BlockchainService blockchainService;

    @GetMapping("/blockchain/genesis")  @CrossOrigin(origins = CORS)
    String genesis(@RequestParam("id") String id,
                   @RequestParam("publicKey") String publicKey,
                   @RequestParam("type") String type,
                   @RequestParam("value") Long value) {
        try {
            Blockchain blockchain = blockchainService.getBlockchain(id);
            if (blockchain != null) {
                blockchain = blockchainService.createGenesisBlock(id, BlockchainType.valueOf(type), value, publicKey);
                return new GsonBuilder().create().toJson(blockchain);
            } else {
                return null;
            }
        } catch (BlockchainException e){
            e.printStackTrace();
            return null;
        }
    }
}

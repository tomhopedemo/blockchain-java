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
public class BlockchainCreateController {

    @Autowired BlockchainService blockchainService;

    @GetMapping("/blockchain/create")  @CrossOrigin(origins = CORS)
    String create(@RequestParam("id") String id,
                  @RequestParam("type") String type) {
        try {
            String blockchainJson = null;
            if (!blockchainService.exists(id)) {
                blockchainService.createBlockchain(id, BlockchainType.valueOf(type));
                blockchainJson = blockchainService.getBlockchainJson(id);
            }
            return blockchainJson;
        } catch (BlockchainException e){
            e.printStackTrace();
            return null;
        }
    }
}

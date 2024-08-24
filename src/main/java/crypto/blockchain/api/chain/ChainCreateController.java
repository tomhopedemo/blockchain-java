package crypto.blockchain.api.chain;

import crypto.blockchain.BlockchainException;
import crypto.blockchain.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.chain.ChainApi.CORS;

@RestController
public class ChainCreateController {

    @Autowired
    ChainService chainService;

    @GetMapping("/chain/create")  @CrossOrigin(origins = CORS)
    String create(@RequestParam("id") String id,
                  @RequestParam("type") String type) throws BlockchainException {
        String blockchainJson = null;
        if (!chainService.exists(id)) {
            chainService.createBlockchain(id, ChainType.valueOf(type));
            blockchainJson = chainService.getBlockchainJson(id);
        }
        return blockchainJson;
    }
}

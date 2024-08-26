package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.Blockchain;
import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class GenesisAPI {
    @GetMapping("/chain/genesis")
    String genesis(@RequestParam("id") String id,
                   @RequestParam("publicKey") String publicKey,
                   @RequestParam("type") String type,
                   @RequestParam("value") Long value)  {
        ChainService chainService = new ChainService();
        Blockchain blockchain = chainService.getChain(id);
            if (blockchain != null) {
                chainService.createGenesisBlock(id, BlockType.valueOf(type), value, publicKey);
            }
            return chainService.getChainJson(id);
    }
}

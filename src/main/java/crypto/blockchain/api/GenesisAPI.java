package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController
public class GenesisAPI {

    ChainService chainService = new ChainService();

    @GetMapping("/chain/genesis")  @CrossOrigin(origins = CORS)
    String genesis(@RequestParam("id") String id,
                   @RequestParam("publicKey") String publicKey,
                   @RequestParam("type") String type,
                   @RequestParam("value") Long value) throws BlockchainException {
            Blockchain blockchain = chainService.getBlockchain(id);
            if (blockchain != null) {
                chainService.createGenesisBlock(id, ChainType.valueOf(type), value, publicKey);
            }
            return chainService.getBlockchainJson(id);
    }
}

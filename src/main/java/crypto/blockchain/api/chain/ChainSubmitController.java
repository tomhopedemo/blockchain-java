package crypto.blockchain.api.chain;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.Data;
import crypto.blockchain.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.chain.ChainApi.CORS;

@RestController
public class ChainSubmitController {
    @Autowired
    ChainService chainService;

    @GetMapping("/simulate")  @CrossOrigin(origins = CORS)
    String simulate(@RequestParam("id") String id,
                    @RequestParam("publicKey") String publicKey,
                    @RequestParam("type") String type,
                    @RequestParam("value") Long value) throws BlockchainException {
            Blockchain blockchain = chainService.getBlockchain(id);
            if (blockchain != null) {
                chainService.simulateBlock(ChainType.valueOf(type), id, Data.getGenesisWallet(id));
            }
            return chainService.getBlockchainJson(id);
    }
}

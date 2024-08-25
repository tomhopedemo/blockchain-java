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
public class SubmitAPI {

    @GetMapping("/transaction/submit")
    String genesis(@RequestParam("id") String id,
                   @RequestParam("type") String type,
                   @RequestParam("transactionJson") String transactionJson){
        ChainService chainService = new ChainService();
        Blockchain blockchain = chainService.getBlockchain(id);
        if (blockchain != null) {
            chainService.submitTransaction(id, BlockType.valueOf(type), transactionJson);
        }
        return chainService.getBlockchainJson(id);
    }
}

package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.Blockchain;
import crypto.blockchain.Request;
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
                   @RequestParam("transactionJson") String requestJson){
        ChainService chainService = new ChainService();
        Blockchain blockchain = chainService.getChain(id);
        if (blockchain != null) {
            BlockType blockType = BlockType.valueOf(type);
            Request request = chainService.deserialiseRequest(blockType, requestJson);
            chainService.submitRequest(id, blockType, request);
        }
        return chainService.getChainJson(id);
    }
}

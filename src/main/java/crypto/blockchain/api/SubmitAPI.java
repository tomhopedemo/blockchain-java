package crypto.blockchain.api;

import crypto.blockchain.*;
import crypto.blockchain.service.ChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController @CrossOrigin(origins = CORS)
public class SubmitAPI {

    @GetMapping("/submit")
    public ResponseEntity<?> submit(@RequestParam("id") String id,
                          @RequestParam("type") String type,
                          @RequestParam("requestJson") String requestJson){
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)) {
            BlockType blockType = BlockType.valueOf(type);
            Request request = chainService.deserialiseRequest(blockType, requestJson);
            chainService.submitRequest(id, blockType, request);
            MinerPool.addMiner(new Miner(id));
            return new ResponseEntity<>(chainService.getChainJson(id), OK);
        } else {
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }
}

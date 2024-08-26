package crypto.blockchain.api;

import crypto.blockchain.service.ChainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class ChainAPI {

    @GetMapping("/chain/create")
    public ResponseEntity<?> create(@RequestParam String id) {
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            chainService.createChain(id);
            return new ResponseEntity<>(id, HttpStatus.OK);
        }
    }


    @GetMapping("/chain/get")
    public String get(@RequestParam("id") String id) {
        return new ChainService().getChainJson(id);
    }


}

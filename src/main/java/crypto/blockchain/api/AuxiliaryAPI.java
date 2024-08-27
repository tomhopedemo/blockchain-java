package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.ChainException;
import crypto.blockchain.service.AuxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController  @CrossOrigin(origins = CORS)
public class AuxiliaryAPI {

    @GetMapping("/auxiliary/keys/add")
    public void addKey(@RequestParam("id") String id, @RequestParam("publicKey") String publicKey, @RequestParam("privateKey") String privateKey){
        new AuxService().addKey(id, publicKey, privateKey);
    }

    @GetMapping("/auxiliary/request/create")
    public ResponseEntity<?> create(@RequestParam("id") String id,
                    @RequestParam("from") String from,
                    @RequestParam("to") String to,
                    @RequestParam("value") Long value,
                    @RequestParam("type") String type
    ) throws ChainException {
        AuxService auxService = new AuxService();
        if (auxService.exists(id)) {

            String requestJson = auxService.createRequestJson(BlockType.valueOf(type), id, from, to, value);
            return new ResponseEntity<>(requestJson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

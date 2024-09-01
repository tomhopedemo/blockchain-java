package crypto.blockchain.api;

import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class KeysAPI {

    @GetMapping("/keys/get")
    String get(@RequestParam("id") String id) {
        return new ChainService().getKeysJson(id);
    }

    //option to save key as well

    @GetMapping("/keys/create")
    String keyPair() {
        return new AuxService().createKeyPairJson();
    }
}

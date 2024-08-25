package crypto.blockchain.api;

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

    @GetMapping("/keys/create")
    String wallet() {
        return new ChainService().createWalletJson();
    }
}
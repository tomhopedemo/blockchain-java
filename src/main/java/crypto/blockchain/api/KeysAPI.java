package crypto.blockchain.api;

import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class KeysAPI {

    //maybe this should be to get the keys which have been uploaded (i.e. saved)
    //or all keys in the system can be added to the cache.
    //however this doesn't really make sense - i guess maintaining and getting the keys
    //isn't worth doing just yet.

    @GetMapping("/keys/get")
    String get(@RequestParam("id") String id) {
        return new ChainService().getKeysJson(id);
    }

    //option to save key as well

    @GetMapping("/keys/create")
    String wallet() {
        return new ChainService().createWalletJson();
    }
}

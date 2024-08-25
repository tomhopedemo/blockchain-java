package crypto.blockchain.api.chain;

import crypto.blockchain.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.chain.ChainApi.CORS;

@RestController
public class KeysController {

    ChainService chainService = new ChainService();

    @GetMapping("/keys/get")  @CrossOrigin(origins = CORS)
    String get(@RequestParam("id") String id) {
        return chainService.getKeysJson(id);
    }

}

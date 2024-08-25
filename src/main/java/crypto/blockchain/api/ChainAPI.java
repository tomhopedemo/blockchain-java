package crypto.blockchain.api;

import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class ChainAPI {

    @GetMapping("/chain/create")
    String create(@RequestParam("id") String id) {
        ChainService chainService = new ChainService();
        if (!chainService.exists(id)) {
            chainService.createChain(id);
            return id;
        }
        return null;
    }
}

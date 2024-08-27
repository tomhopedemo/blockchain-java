package crypto.blockchain.api;

import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class ValidationAPI {

    @GetMapping("/validation")
    public boolean get(@RequestParam("id") String id) {
        return new AuxService().validate(id);
    }

}

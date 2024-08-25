package crypto.blockchain.api.auxillary;

import crypto.blockchain.BlockchainException;
import crypto.blockchain.api.chain.ChainType;
import crypto.blockchain.service.AuxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    //trusted process to handle your keys + generate transactions
    public static final String CORS = "http://localhost:3000";

    AuxService auxService = new AuxService();

    @GetMapping("/keys/add") @CrossOrigin(origins = CORS)
    void addKey(@RequestParam("id") String id, @RequestParam("publicKey") String publicKey, @RequestParam("privateKey") String privateKey){
        auxService.addKey(id, publicKey, privateKey);
    }

    @GetMapping("/request/create")  @CrossOrigin(origins = CORS)
    String create(@RequestParam("id") String id,
                    @RequestParam("from") String from,
                    @RequestParam("to") String to,
                    @RequestParam("value") Long value,
                    @RequestParam("type") String type

    ) throws BlockchainException {
        if (auxService.exists(id)) {
            return auxService.createRequestJson(ChainType.valueOf(type), id, from, to, value);
        }
        return null;
    }
}

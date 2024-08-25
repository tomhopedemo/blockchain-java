package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.service.AuxService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController  @CrossOrigin(origins = CORS)
public class AuxiliaryAPI {

    @GetMapping("/auxiliary/keys/add")
    void addKey(@RequestParam("id") String id, @RequestParam("publicKey") String publicKey, @RequestParam("privateKey") String privateKey){
        new AuxService().addKey(id, publicKey, privateKey);
    }

    @GetMapping("/auxiliary/transaction/create")
    String create(@RequestParam("id") String id,
                    @RequestParam("from") String from,
                    @RequestParam("to") String to,
                    @RequestParam("value") Long value,
                    @RequestParam("type") String type
    ) throws BlockchainException {
        AuxService auxService = new AuxService();
        if (auxService.exists(id)) {
            return auxService.createRequestJson(BlockType.valueOf(type), id, from, to, value);
        }
        return null;
    }
}

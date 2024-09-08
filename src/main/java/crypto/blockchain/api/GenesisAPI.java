package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.Blockchain;
import crypto.blockchain.ChainException;
import crypto.blockchain.Request;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class GenesisAPI {
    @GetMapping("/chain/genesis")
    public ResponseEntity<?> genesis(@RequestParam("id") String id,
                   @RequestParam("publicKey") String publicKey,
                   @RequestParam("currency") String currency,
                   @RequestParam("type") String type,
                   @RequestParam("value") Long value) {
        ChainService chainService = new ChainService();
        Blockchain chain = chainService.getChain(id);
        if (chain != null) {
            BlockType blockType = BlockType.valueOf(type);
            try {
                Request request = new AuxService().genesisRequest(id, blockType, publicKey, currency, value);
                chainService.submitRequest(id, request);
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (ChainException ignored){
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

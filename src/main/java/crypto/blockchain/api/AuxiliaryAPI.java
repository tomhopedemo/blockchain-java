package crypto.blockchain.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.BlockType;
import crypto.blockchain.ChainException;
import crypto.blockchain.Request;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.service.AuxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static crypto.blockchain.api.Control.CORS;

@RestController  @CrossOrigin(origins = CORS)
public class AuxiliaryAPI {

    static Gson JSON = new GsonBuilder().create();

    @GetMapping("/auxiliary/keys/add")
    public void addKey(@RequestParam("id") String id,
                       @RequestParam("publicKey") String publicKey,
                       @RequestParam("privateKey") String privateKey){
        new AuxService().registerKeyPair(id, publicKey, privateKey);
    }

    @GetMapping("/auxiliary/request/transactional/create")
    public ResponseEntity<?> createTransactional(@RequestParam("id") String id,
                                                 @RequestParam("type") String type,
                                                 @RequestParam("transactionRequestParams") TransactionRequestParams transactionRequestParams
    )  {
        AuxService auxService = new AuxService();
        if (auxService.exists(id)) {
            try {
                BlockType blockType = BlockType.valueOf(type);
                Request request = auxService.createTransactionRequest(id, type, transactionRequestParams);
                if (request != null) {
                    String requestJson = JSON.toJson(request, blockType.getRequestClass());
                    return new ResponseEntity<>(requestJson, HttpStatus.OK);
                }
            } catch (ChainException ignored){
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/auxiliary/request/data/create")
    public ResponseEntity<?> createData(@RequestParam("id") String id,
                    @RequestParam("key") String key,
                    @RequestParam("value") String value,
                    @RequestParam("type") String type
    )  {
        AuxService auxService = new AuxService();
        if (auxService.exists(id)) {
            try {
                BlockType blockType = BlockType.valueOf(type);
                Request request = auxService.createDataRequest(blockType, id, key, value);
                if (request != null) {
                    String requestJson = JSON.toJson(request, blockType.getRequestClass());
                    return new ResponseEntity<>(requestJson, HttpStatus.OK);
                }
            } catch (ChainException ignored){
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

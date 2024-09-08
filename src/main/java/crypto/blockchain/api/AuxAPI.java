package crypto.blockchain.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.blockchain.BlockType;
import crypto.blockchain.ChainException;
import crypto.blockchain.Request;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.service.AuxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.BlockType.*;
import static crypto.blockchain.api.Control.CORS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController  @CrossOrigin(origins = CORS)
public class AuxAPI {
    static Gson JSON = new GsonBuilder().create();
    static ResponseEntity<Object> ERROR = new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    AuxService auxService = new AuxService();

    @GetMapping("/reg/keypair")
    public void addKey(@RequestParam("public") String publicKey,
                       @RequestParam("private") String privateKey){
        new AuxService().registerKeyPair(publicKey, privateKey);
    }

    @GetMapping("/r/account")
    public ResponseEntity<?> account(@RequestParam("id") String id,
                                     @RequestParam("params") TransactionRequestParams params)  {
        return create(id, ACCOUNT, () -> auxService.account(id, params));
    }

    @GetMapping("/r/currency")
    public ResponseEntity<?> currency(@RequestParam("id") String id,
                                      @RequestParam("key") String key,
                                      @RequestParam("value") String value) {
        return create(id, CURRENCY, () -> auxService.currency(id, key, value));
    }

    @GetMapping("/r/keypair")
    public ResponseEntity<?> keypair() {
        return create(null, CURRENCY, () -> auxService.keypair());
    }

    @GetMapping("/r/signed")
    public ResponseEntity<?> signed(@RequestParam("id") String id,
                                    @RequestParam("key") String key,
                                    @RequestParam("data") String data) {
        return create(id, CURRENCY, () -> auxService.signed(id, key, data));
    }

    @GetMapping("/r/simple")
    public ResponseEntity<?> simple(@RequestParam("id") String id,
                                    @RequestParam("data") String data) {
        return create(id, CURRENCY, () -> auxService.simple(data));
    }


    @GetMapping("/r/utxo")
    public ResponseEntity<?> createUtxo(@RequestParam("id") String id,
                                        @RequestParam("params") TransactionRequestParams params)  {
        return create(id, UTXO, () -> auxService.utxo(id, params));
    }


    public interface CreateRequest {
        Request create() throws ChainException;
    }

    public static ResponseEntity<?> create(String id, BlockType type, CreateRequest create) {
        AuxService auxService = new AuxService();
        if (!auxService.exists(id)) return ERROR;
        try {
            Request request = create.create();
            if (request == null) return ERROR;
            return new ResponseEntity<>(JSON.toJson(request, type.getRequestClass()), OK);
        } catch (ChainException e){
            e.printStackTrace();
            return ERROR;
        }
    }
}

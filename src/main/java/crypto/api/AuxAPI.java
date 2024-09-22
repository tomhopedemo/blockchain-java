package crypto.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.ChainException;
import crypto.Request;
import crypto.block.*;
import crypto.service.AuxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static crypto.api.Control.CORS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController  @CrossOrigin(origins = CORS)
public class AuxAPI {
    static Gson JSON = new GsonBuilder().create();
    static ResponseEntity<Object> ERROR = new ResponseEntity<>(INTERNAL_SERVER_ERROR);

    @GetMapping("/reg/keypair")
    public void addKey(@RequestParam("public") String publicKey,
                       @RequestParam("private") String privateKey){
        new AuxService(null).registerKeypair(publicKey, privateKey);
    }

    @GetMapping("/r/account")
    public ResponseEntity<?> account(@RequestParam("stockKey") String id,
                                     @RequestParam("from") String from,
                                     @RequestParam("to") String to,
                                     @RequestParam("currency") String currency,
                                     @RequestParam("value") String value)  {
        return create(id, Transaction.class, () -> new AuxService(id).account(from, to, currency, Long.valueOf(value)));
    }

    @GetMapping("/r/currency")
    public ResponseEntity<?> currency(@RequestParam("stockKey") String id,
                                      @RequestParam("key") String key,
                                      @RequestParam("value") String value) {
        return create(id, Currency.class, () -> new AuxService(id).currency(key, value));
    }

    @GetMapping("/r/keypair")
    public ResponseEntity<?> keypair() {
        return create(null, Keypair.class, () -> new AuxService(null).keypair());
    }

    @GetMapping("/r/data")
    public ResponseEntity<?> data(@RequestParam("stockKey") String id,
                                    @RequestParam("key") String key,
                                    @RequestParam("data") byte[] data,
                                    @RequestParam("format") String format) {
        return create(id, Data.class, () -> new AuxService(id).data(key, data, format));
    }

    @GetMapping("/r/utxo")
    public ResponseEntity<?> createUtxo(@RequestParam("stockKey") String id,
                                        @RequestParam("from") String from,
                                        @RequestParam("to") String to,
                                        @RequestParam("currency") String currency,
                                        @RequestParam("value") String value)  {
        return create(id, UTXO.class, () -> new AuxService(id).utxo(from, to, currency, Long.valueOf(value)));
    }


    @GetMapping("/validate")
    public ResponseEntity<?> get(@RequestParam("stockKey") String id) {
        boolean validate = new AuxService(id).validate();
        return new ResponseEntity<>(validate, OK);
    }

    public interface CreateRequest {
        Request create() throws ChainException;
    }

    public static ResponseEntity<?> create(String id, Class<? extends Request> type, CreateRequest create) {
        AuxService auxService = new AuxService(id);
        if (!auxService.exists()) return ERROR;
        try {
            Request request = create.create();
            if (request == null) return ERROR;
            return new ResponseEntity<>(JSON.toJson(request, type), OK);
        } catch (ChainException e){
            e.printStackTrace();
            return ERROR;
        }
    }
}

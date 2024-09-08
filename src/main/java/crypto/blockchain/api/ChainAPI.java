package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.Simulator;
import crypto.blockchain.service.ChainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static crypto.blockchain.api.Control.CORS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController @CrossOrigin(origins = CORS)
public class ChainAPI {

    ChainService chainService = new ChainService();
    static ResponseEntity<Object> ERROR = new ResponseEntity<>(INTERNAL_SERVER_ERROR);

    @GetMapping("/chain/create")
    public ResponseEntity<?> create(@RequestParam("id") String id) {
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)) return ERROR;
        chainService.createChain(id);
        String chainJson = chainService.getChainJson(id);
        return new ResponseEntity<>(chainJson, OK);
    }

    @GetMapping("/chain/get")
    public String get(@RequestParam("id") String id) {
        return new ChainService().getChainJson(id);
    }

    @GetMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestParam("type") String type) {
        String id = UUID.randomUUID().toString();
        chainService.createChain(id);
        chainService.disableAutoMining(id);
        Simulator service = new Simulator(id);
        try {
            switch (BlockType.valueOf(type)) {
                case ACCOUNT -> {
                    service.keypair();
                    service.currency();
                    service.account();
                }
                case CURRENCY -> {
                    service.keypair();
                    service.currency();
                }
                case KEYPAIR -> service.keypair();
                case SIGNED_DATA -> service.signed();
                case DATA -> service.simple();
                case UTXO -> service.utxo();
            }
            return new ResponseEntity<>(chainService.getChainJson(id), OK);
        } catch (Exception e){
            e.printStackTrace();
            return ERROR;
        }
    }



}

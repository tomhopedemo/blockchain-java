package crypto.blockchain.api;

import crypto.blockchain.BlockType;
import crypto.blockchain.Simulator;
import crypto.blockchain.service.ChainService;
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

    static ResponseEntity<Object> ERROR = new ResponseEntity<>(INTERNAL_SERVER_ERROR);

    @GetMapping("/chain/create")
    public ResponseEntity<?> create(@RequestParam("id") String id) {
        ChainService chainService = new ChainService(id);
        if (chainService.hasChain()) return ERROR;
        chainService.createChain();
        String chainJson = chainService.getChainJson();
        return new ResponseEntity<>(chainJson, OK);
    }

    @GetMapping("/chain/get")
    public String get(@RequestParam("id") String id) {
        return new ChainService(id).getChainJson();
    }

    @GetMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestParam("type") String type) {
        String id = UUID.randomUUID().toString();
        ChainService chainService = new ChainService(id);
        chainService.createChain();
        chainService.disableAutoMining();
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
                case DATA -> service.data();
                case UTXO -> service.utxo();
            }
            return new ResponseEntity<>(chainService.getChainJson(), OK);
        } catch (Exception e){
            e.printStackTrace();
            return ERROR;
        }
    }



}

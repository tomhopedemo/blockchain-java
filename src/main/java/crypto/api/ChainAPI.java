package crypto.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.*;
import crypto.block.*;
import crypto.service.AuxService;
import crypto.service.ChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static crypto.api.Control.CORS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController @CrossOrigin(origins = CORS)
public class ChainAPI {

    static Gson JSON = new GsonBuilder().create();
    static ResponseEntity<Object> ERROR = new ResponseEntity<>(INTERNAL_SERVER_ERROR);

    @GetMapping("/c/new")
    public ResponseEntity<?> create(@RequestParam("id") String id) {
        ChainService chainService = new ChainService(id);
        if (chainService.hasChain()) return ERROR;
        chainService.createChain();
        String chainJson = chainService.getChainJson();
        return new ResponseEntity<>(chainJson, OK);
    }

    @GetMapping("/c/get")
    public String get(@RequestParam("id") String id) {
        return new ChainService(id).getChainJson();
    }

    @GetMapping("/c/simulate")
    //i guess there is no reason why we can't pull the types from the directory
    public ResponseEntity<?> simulate(@RequestParam("type") String type) {
        String id = UUID.randomUUID().toString();
        ChainService chainService = new ChainService(id);
        chainService.createChain();
        chainService.disableAutoMining();
        Simulator simulator = new Simulator(id);
        try {
            switch (type) {
                case "branch" -> {
                    Keypair keypair = simulator.keypair();
                    simulator.branch(keypair);
                }
                case "create" -> {
                    simulator.create();
                }
                case "currency" -> {
                    Keypair keypair = simulator.keypair();
                    simulator.currency(keypair);
                }
                case "data" -> simulator.data();
                case "difficulty" -> {
                    Keypair keypair = simulator.keypair();
                    String currency = simulator.currency(keypair);
                    simulator.difficulty(currency, keypair);
                }
                case "hash" -> {
                    Keypair keypair = simulator.keypair();
                    simulator.hash(keypair);
                }
                case "keypair" -> simulator.keypair();
                case "merge" -> {
                    Keypair keypair = simulator.keypair();
                    Branch branch = simulator.branch(keypair);
                    simulator.merge(keypair, branch.branchKey());
                }
                case "publish" -> {
                    Keypair keypair = simulator.create();
                    simulator.publish(keypair);
                }
                case "referendum" -> {
                    simulator.referendum();
                }
                case "stake" -> {
                    Keypair keypair = simulator.keypair();
                    simulator.currency(keypair);
                    Keypair accountKeypair = simulator.account(keypair);
                    simulator.stake(accountKeypair);
                }
                case "transaction" -> {
                    Keypair keypair = simulator.keypair();
                    simulator.currency(keypair);
                    simulator.account(keypair);
                }
                case "utxo" -> simulator.utxo();
            }
            return new ResponseEntity<>(chainService.getChainJson(), OK);
        } catch (Exception e){
            e.printStackTrace();
            return ERROR;
        }
    }

    @GetMapping("/c/add")
    public ResponseEntity<?> submit(@RequestParam("id") String id,
                                    @RequestParam("type") String type,
                                    @RequestParam("requestJson") String requestJson){
        ChainService chainService = new ChainService(id);
        if (!chainService.hasChain()) return ERROR;
        Class<? extends Request> clazz;
        try {
            clazz = (Class<? extends Request>) Class.forName("crypto.block." + type);
        } catch (ClassNotFoundException e) {
            return ERROR;
        }
        Request request = JSON.fromJson(requestJson, clazz);
        chainService.submitRequest(request);
        chainService.requestMiner();
        return new ResponseEntity<>(chainService.getChainJson(), OK);
    }

    @GetMapping("/c/genesis")
    public ResponseEntity<?> genesis(@RequestParam("id") String id,
                                     @RequestParam("type") String type,
                                     @RequestParam("key") String publicKey,
                                     @RequestParam("currency") String currency,
                                     @RequestParam("value") Long value) {
        ChainService chainService = new ChainService(id);
        Blockchain chain = chainService.getChain();
        if (chain == null) return ERROR;
        Request request = new AuxService(id).utxoGenesis(publicKey, currency, value);
        chainService.submitRequest(request);
        return new ResponseEntity<>(OK);
    }
}

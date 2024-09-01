package crypto.blockchain.api;

import crypto.blockchain.*;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class SimulateAPI {

    public static final String CURRENCY = "CBX";

    @GetMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestParam("type") String type) {
        BlockType blockType = BlockType.valueOf(type);
        String id = randomString(5);
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        chainService.createChain(id);
        chainService.disableAutoMining(id);
        chainService.allowBlockType(id, blockType);

        try {
            return switch (blockType) {
                case DATA -> simulateData(id);
                case SIGNED_DATA -> simulateSignedData(id);
                case CURRENCY -> simulateCurrency(id);
                case ACCOUNT -> simulateTransactional(id, BlockType.ACCOUNT);
                case UTXO -> simulateTransactional(id, BlockType.UTXO);
            };
        } catch (ChainException ignored){
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> simulateData(String id)  {
        BlockType blockType = BlockType.DATA;
        ChainService chainService = new ChainService();

        Request genesis = new DataRequest("ABCDE");
        chainService.submitRequest(id, blockType, genesis);
        Miner miner = new Miner(id);
        miner.runSynch();

        Request request = new DataRequest(randomString(10));
        chainService.submitRequest(id, blockType, request);
        miner.runSynch();

        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }

    private ResponseEntity<?> simulateCurrency(String id) {
        BlockType blockType = BlockType.CURRENCY;
        ChainService chainService = new ChainService();
        chainService.allowBlockType(id, blockType);
        AuxService auxService = new AuxService();
        KeyPair keyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());
        CurrencyRequest currencyRequest = new CurrencyRequest(CURRENCY, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());
        chainService.submitRequest(id, blockType, currencyRequest);
        Miner miner = new Miner(id);
        miner.runSynch();
        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }



    private ResponseEntity<?> simulateSignedData(String id) throws ChainException {
        BlockType blockType = BlockType.SIGNED_DATA;
        ChainService chainService = new ChainService();
        AuxService auxService = new AuxService();
        KeyPair keyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());

        Optional<? extends Request> genesisRequest = auxService.createGenesisRequest(id, blockType, keyPair.getPublicKeyAddress(), null, "ABCDE");
        if (genesisRequest.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, genesisRequest.get());
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair anotherKeyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, anotherKeyPair.getPublicKeyAddress(), anotherKeyPair.getPrivateKey());
        Optional<? extends Request> request = auxService.createRequest(blockType, id, anotherKeyPair.getPublicKeyAddress(), null, null, "GHIJK");
        if (request.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, request.get());
        miner.runSynch();
        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }

    private ResponseEntity<?> simulateTransactional(String id, BlockType blockType) throws ChainException {
        simulateCurrency(id);

        ChainService chainService = new ChainService();
        AuxService auxService = new AuxService();

        KeyPair keyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());

        Optional<? extends Request> genesisRequest = auxService.createGenesisRequest(id, blockType, keyPair.getPublicKeyAddress(), CURRENCY, 100L);
        if (genesisRequest.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, genesisRequest.get());
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair toKeyPair = auxService.createKeyPair();
        Optional<? extends Request> request = auxService.createRequest(blockType, id, keyPair.getPublicKeyAddress(), toKeyPair.getPublicKeyAddress(), CURRENCY, 5L);
        if (request.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, request.get());
        miner.runSynch();

        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }

    private String randomString(int length){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

}

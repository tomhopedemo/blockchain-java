package crypto.blockchain.api;

import crypto.blockchain.*;
import crypto.blockchain.api.data.TransactionRequestParams.Builder;
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
        if (chainService.hasChain(id)){ //let's do this properly
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        chainService.createChain(id);
        chainService.disableAutoMining(id);
        chainService.allowBlockType(id, blockType);

        try {
            switch (blockType) {
                case DATA -> simulateData(id);
                case SIGNED_DATA -> simulateSignedData(id);
                case CURRENCY -> simulateCurrency(id);
                case ACCOUNT -> {
                    simulateCurrency(id);
                    simulateTransactional(id, BlockType.ACCOUNT);
                }
                case UTXO -> simulateTransactional(id, BlockType.UTXO);
            }
            return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void simulateData(String id)  {
        BlockType blockType = BlockType.DATA;
        ChainService chainService = new ChainService();

        Request genesis = new DataRequest("ABCDE");
        chainService.submitRequest(id, blockType, genesis);
        Miner miner = new Miner(id);
        miner.runSynch();

        Request request = new DataRequest(randomString(10));
        chainService.submitRequest(id, blockType, request);
        miner.runSynch();
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



    private void simulateSignedData(String id) throws ChainException {
        BlockType blockType = BlockType.SIGNED_DATA;
        ChainService chainService = new ChainService();
        AuxService auxService = new AuxService();
        KeyPair keyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());
        Request request = auxService.createDataRequest(blockType, id, keyPair.getPublicKeyAddress(), "ABCDE");
        chainService.submitRequest(id, blockType, request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    private void simulateTransactional(String id, BlockType blockType) throws ChainException {
        ChainService chainService = new ChainService();
        AuxService auxService = new AuxService();

        KeyPair keyPair = auxService.createKeyPair();
        auxService.registerKeyPair(id, keyPair.getPublicKeyAddress(), keyPair.getPrivateKey());

        Request genesisRequest = auxService.createGenesisRequest(id, blockType, keyPair.getPublicKeyAddress(), CURRENCY, 100L);
        chainService.submitRequest(id, blockType, genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair toKeyPair = auxService.createKeyPair();
        Request request = auxService.createTransactionRequest(id, blockType.name(),
                new Builder()
                        .setCurrency(CURRENCY)
                        .setFrom(keyPair.getPublicKeyAddress())
                        .setTo(toKeyPair.getPublicKeyAddress())
                        .setValue(5L)
                        .build());
        chainService.submitRequest(id, blockType, request);
        miner.runSynch();
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

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
    @GetMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestParam("type") String type) throws ChainException {
        BlockType blockType = BlockType.valueOf(type);
        String id = randomString(5);
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        chainService.createChain(id);
        chainService.allowBlockType(id, blockType);
        ResponseEntity<?> responseEntity = switch(blockType){
            case DATA -> simulateData(id);
            case SIGNED_DATA -> simulateSignedData(id);
            case ACCOUNT -> simulateTransactional(id, BlockType.ACCOUNT);
            case UTXO -> simulateTransactional(id, BlockType.UTXO);
        };
        new Miner(id).run(); // synchronously.
        return responseEntity;
    }


    private ResponseEntity<?> simulateData(String id)  {
        ChainService chainService = new ChainService();

        Request genesis = new DataRequest("ABCDE");
        chainService.submitRequest(id, BlockType.DATA, genesis);

        //note that we may want to wait for the genesis block to be created
        //also wait a certain amount of time like 5 seconds max.
        //wait for something to be visible in the chain

        Request request = new DataRequest(randomString(10));
        chainService.submitRequest(id, BlockType.DATA, request);

        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }

    private ResponseEntity<?> simulateSignedData(String id) throws ChainException {
        ChainService chainService = new ChainService();
        AuxService auxService = new AuxService();
        Wallet wallet = chainService.createWallet();
        Wallet anotherWallet = chainService.createWallet();

        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());

        Optional<? extends Request> genesisRequest = auxService.createGenesisRequest(id, BlockType.SIGNED_DATA, wallet.getPublicKeyAddress(), "ABCDE");
        if (genesisRequest.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, BlockType.SIGNED_DATA, genesisRequest.get());

        auxService.addKey(id, anotherWallet.getPrivateKey(), anotherWallet.getPublicKeyAddress());
        Optional<? extends Request> request = auxService.createRequest(BlockType.SIGNED_DATA, id, anotherWallet.getPublicKeyAddress(), null, "GHIJK");
        if (request.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, BlockType.SIGNED_DATA, request.get());
        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }


    private ResponseEntity<?> simulateTransactional(String id, BlockType blockType) throws ChainException {
        ChainService chainService = new ChainService();
        Wallet wallet = chainService.createWallet();
        Data.addWallet(id, wallet);

        AuxService auxService = new AuxService();
        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());

        Optional<? extends Request> genesisRequest = auxService.createGenesisRequest(id, blockType, wallet.getPublicKeyAddress(), 100L);
        if (genesisRequest.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, genesisRequest.get());

        Wallet toWallet = chainService.createWallet();
        Optional<? extends Request> request = auxService.createRequest(blockType, id, wallet.getPublicKeyAddress(), toWallet.getPublicKeyAddress(), 5L);
        if (request.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, blockType, request.get());
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

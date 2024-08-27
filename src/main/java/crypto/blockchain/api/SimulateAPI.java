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
    public ResponseEntity<?> simulate(@RequestParam("type") String type) throws BlockchainException {
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
        Wallet wallet = chainService.createWallet();

        chainService.createGenesisBlock(id, BlockType.DATA, "ABCDE", wallet.getPublicKeyAddress());

        AuxService auxService = new AuxService();
        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());

        Request request = new DataRequest(randomString(10));

        chainService.submitRequest(id, BlockType.DATA, request);

        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }

    private ResponseEntity<?> simulateSignedData(String id) throws BlockchainException {
        ChainService chainService = new ChainService();
        Wallet wallet = chainService.createWallet();
        Wallet anotherWallet = chainService.createWallet();

        chainService.createGenesisBlock(id, BlockType.SIGNED_DATA, "ABCDE", wallet.getPublicKeyAddress());

        AuxService auxService = new AuxService();

        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());
        auxService.addKey(id, anotherWallet.getPrivateKey(), anotherWallet.getPublicKeyAddress());

        Optional<? extends Request> request = auxService.createRequest(BlockType.SIGNED_DATA, id, anotherWallet.getPublicKeyAddress(), null, "GHIJK");
        if (request.isEmpty()){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        chainService.submitRequest(id, BlockType.SIGNED_DATA, request.get());
        return new ResponseEntity<>(chainService.getChainJson(id), HttpStatus.OK);
    }


    private ResponseEntity<?> simulateTransactional(String id, BlockType blockType) throws BlockchainException {
        ChainService chainService = new ChainService();
        Wallet wallet = chainService.createWallet();

        chainService.createGenesisBlock(id, blockType, 100L, wallet.getPublicKeyAddress());

        AuxService auxService = new AuxService();
        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());

        Wallet toWallet = chainService.createWallet();
        Optional<? extends Request> request = auxService.createRequest(blockType, id, wallet.getPublicKeyAddress(), toWallet.getPublicKeyAddress(), 5);
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

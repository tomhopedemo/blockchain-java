package crypto.blockchain.api;

import crypto.blockchain.*;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class SimulateAPI {
    @GetMapping("/simulate")
    String simulate() throws BlockchainException {
        String id = randomId();
        ChainService chainService = new ChainService();
        if (chainService.hasChain(id)){
            throw new BlockchainException("Unable to simulate chain");
        }

        Wallet wallet = chainService.createWallet();

        chainService.createChain(id);
        chainService.allowBlockType(id, BlockType.ACCOUNT);
        chainService.createGenesisBlock(id, BlockType.ACCOUNT, 100L, wallet.getPublicKeyAddress());

        AuxService auxService = new AuxService();
        auxService.addKey(id, wallet.getPrivateKey(), wallet.getPublicKeyAddress());

        Wallet toWallet = chainService.createWallet();
        Request request = auxService.createRequest(BlockType.ACCOUNT, id, wallet.getPublicKeyAddress(), toWallet.getPublicKeyAddress(), 5);

        chainService.submitRequest(id, BlockType.ACCOUNT, request);

        return chainService.getChainJson(id);
    }

    private String randomId(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }
}

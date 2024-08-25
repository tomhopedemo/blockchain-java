package crypto.blockchain.api;

import crypto.blockchain.*;
import crypto.blockchain.service.ChainService;
import crypto.blockchain.simple.StringHashable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

import static crypto.blockchain.api.Control.CORS;

@RestController @CrossOrigin(origins = CORS)
public class SimulateAPI {
    @GetMapping("/simulate")
    String simulate() throws BlockchainException {
        String id = randomId();
        ChainService chainService = new ChainService();
        Blockchain blockchain = chainService.getBlockchain(id);
        if (blockchain != null) {
            Wallet wallet = chainService.createWallet();

            chainService.createChain(id);
            chainService.allowBlockType(id, BlockType.ACCOUNT);
            chainService.createGenesisBlock(id, BlockType.ACCOUNT, 100L, wallet.getPublicKeyAddress());
            chainService.simulateBlock(id, BlockType.ACCOUNT, wallet);
        }
        return chainService.getBlockchainJson(id);
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

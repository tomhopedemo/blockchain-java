package crypto.blockchain.api;

import com.google.gson.GsonBuilder;
import crypto.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static crypto.blockchain.api.ApiApplication.CORS;

@RestController
public class WalletController {

    @GetMapping("/wallet/create") @CrossOrigin(origins = CORS)
    String wallet() {
        Wallet wallet = Wallet.generate();
        return new GsonBuilder().create().toJson(wallet);
    }


}

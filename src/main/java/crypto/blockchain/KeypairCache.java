package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeypairCache {

    private final List<Keypair> keypairs = new ArrayList<>();

    public Keypair getKeypair(String publicKey){
        Optional<Keypair> keypair = keypairs.stream().filter(w -> publicKey.equals(w.publicKey())).findFirst();
        return keypair.isPresent() ? keypair.get() : null;
    }

    public List<Keypair> getKeypairs(){
        return this.keypairs;
    }

    public void addKeypair(Keypair keypair){
        keypairs.add(keypair);
    }

}

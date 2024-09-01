package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeyPairCache {

    private final List<KeyPair> keyPairs = new ArrayList<>();

    public Optional<KeyPair> getKeyPair(String publicKey){
        return keyPairs.stream().filter(w -> publicKey.equals(w.getPublicKeyAddress())).findAny();
    }

    public List<KeyPair> getKeyPairs(){
        return this.keyPairs;
    }

    public void addKeyPair(KeyPair keyPair){
        keyPairs.add(keyPair);
    }

}

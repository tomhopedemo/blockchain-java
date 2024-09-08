package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeyPairCache {

    private final List<KeyPair> keyPairs = new ArrayList<>();

    public KeyPair getKeyPair(String publicKey){
        Optional<KeyPair> keyPairOptional = keyPairs.stream().filter(w -> publicKey.equals(w.publicKey())).findFirst();
        return keyPairOptional.isPresent() ? keyPairOptional.get() : null;
    }

    public List<KeyPair> getKeyPairs(){
        return this.keyPairs;
    }

    public void addKeyPair(KeyPair keyPair){
        keyPairs.add(keyPair);
    }

}
package crypto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuxData {

    private static final Map<String, Keypair> keypairs = new ConcurrentHashMap<>();

    public static Keypair getKeypair(String publicKey) {
        return keypairs.get(publicKey);
    }

    public static void addKeypair(Keypair keypair) {
        keypairs.putIfAbsent(keypair.publicKey(), keypair);
    }
}

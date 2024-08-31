package crypto.blockchain;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Signing {

    public static byte[] sign(Wallet wallet, String hash) throws ChainException {
        byte[] preSignature = hash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new ChainException(e);
        }
    }
}

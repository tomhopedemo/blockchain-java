package crypto.blockchain.signed;

import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SignedDataRequestVerification {
    public static boolean verifySignature(SignedDataRequest signedDataRequest) {
        String valueHash = signedDataRequest.generateValueHash();
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(signedDataRequest.getPublicKeyAddress());
            return ECDSA.verifyECDSASignature(publicKey, valueHash.getBytes(UTF_8), Hex.decode(signedDataRequest.getSignature()));
        } catch (GeneralSecurityException e){
            return false;
        }
    }
}

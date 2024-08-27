package crypto.blockchain.signed;

import crypto.blockchain.ChainException;
import crypto.blockchain.Wallet;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SignedDataRequestFactory {

    public static Optional<SignedDataRequest> createSignedDataRequest(Wallet wallet, String value) throws ChainException {
        SignedDataRequest signedDataRequest = new SignedDataRequest(wallet.getPublicKeyAddress(), value);
        byte[] signature = calculateSignature(signedDataRequest, wallet);
        signedDataRequest.setSignature(signature);
        return Optional.of(signedDataRequest);
    }

    public static byte[] calculateSignature(SignedDataRequest signedDataRequest, Wallet wallet) throws ChainException {
        byte[] preSignature = signedDataRequest.generateValueHash().getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new ChainException(e);
        }
    }


}

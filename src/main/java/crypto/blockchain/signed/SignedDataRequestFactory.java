package crypto.blockchain.signed;

import crypto.blockchain.ChainException;
import crypto.blockchain.Signing;
import crypto.blockchain.KeyPair;

import java.util.Optional;

public class SignedDataRequestFactory {

    public static Optional<SignedDataRequest> createSignedDataRequest(KeyPair keyPair, String value) throws ChainException {
        SignedDataRequest signedDataRequest = new SignedDataRequest(keyPair.getPublicKeyAddress(), value);
        byte[] signature = Signing.sign(keyPair, signedDataRequest.generateValueHash());
        signedDataRequest.setSignature(signature);
        return Optional.of(signedDataRequest);
    }

}

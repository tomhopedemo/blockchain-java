package crypto.blockchain.signed;

import crypto.blockchain.ChainException;
import crypto.blockchain.Request;
import crypto.blockchain.Signing;
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
        byte[] signature = Signing.sign(wallet, signedDataRequest.generateValueHash());
        signedDataRequest.setSignature(signature);
        return Optional.of(signedDataRequest);
    }

}

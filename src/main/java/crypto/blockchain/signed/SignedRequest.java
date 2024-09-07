package crypto.blockchain.signed;

import crypto.blockchain.Request;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;


public record SignedRequest (String publicKeyAddress, String value, String signature) implements Request {

    @Override
    public String getBlockDataHash() {
        byte[] hash = Hashing.hash(signature);
        return Encoder.encodeToHexadecimal(hash);
    }

    public static String generateHash(String publicKey, String value) {
        String preHash = publicKey + "~" + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

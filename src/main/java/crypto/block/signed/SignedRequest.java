package crypto.block.signed;

import crypto.blockchain.Request;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;


public record SignedRequest (String publicKey, String value, String signature) implements Request {

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String publicKey, String value) {
        String preHash = publicKey + "~" + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

package crypto.block.stake;

import crypto.Request;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public record StakeRequest(String publicKey, String currency, long value, int index, String signature) implements Request {

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String publicKey, String currency, long value, int index) {
        String preHash = publicKey + "~" + currency + "~" + value + "~" + index;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

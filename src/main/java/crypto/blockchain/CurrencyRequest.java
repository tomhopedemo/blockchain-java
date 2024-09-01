package crypto.blockchain;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;


public record CurrencyRequest(String currency, String publicKey, String privateKey) implements BlockDataHashable, Request {

    @Override
    public String getBlockDataHash() {
        String preHash = publicKey + "~" + currency + "~" + privateKey;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

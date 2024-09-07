package crypto.blockchain;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public interface BlockDataHashable {

    String getPreHash() ;

    default String getBlockDataHash() {
        String preHash = getPreHash();
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

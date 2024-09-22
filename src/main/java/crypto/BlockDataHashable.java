package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public interface BlockDataHashable {

    String getPreHash() ;

    default String getBlockDataHash(Hashing.Type hashType) {
        String preHash = getPreHash();
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }

}

package crypto.blockchain.signed;

import crypto.blockchain.Block;
import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public record BlockDataWrapper<T extends BlockDataHashable> (List<T> blockData) implements BlockDataHashable {

    @Override
    public String getBlockDataHash()  {
        String preHash = String.join("", blockData.stream().map(data -> data.getBlockDataHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

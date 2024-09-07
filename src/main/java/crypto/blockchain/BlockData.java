package crypto.blockchain;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public record BlockData<T extends BlockDataHashable> (List<T> data) implements BlockDataHashable {

    @Override
    public String getPreHash()  {
        return String.join("", data.stream().map(data -> data.getBlockDataHash()).toList());
    }

}

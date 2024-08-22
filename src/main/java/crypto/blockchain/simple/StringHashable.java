package crypto.blockchain.simple;

import crypto.blockchain.BlockDataHashable;

public record StringHashable (String string) implements BlockDataHashable {

    @Override
    public String blockDataHash() {
        return string;
    }
}

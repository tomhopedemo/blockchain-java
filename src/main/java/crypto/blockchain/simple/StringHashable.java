package crypto.blockchain.simple;

import crypto.blockchain.BlockDataHashable;

public record StringHashable (String string) implements BlockDataHashable {

    //this shoudl contain reference to the requests

    @Override
    public String getBlockDataHash() {
        return string;
    }
}

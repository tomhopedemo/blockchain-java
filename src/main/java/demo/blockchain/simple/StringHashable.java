package demo.blockchain.simple;

import demo.blockchain.BlockDataHashable;

public class StringHashable implements BlockDataHashable {

    String string;

    public StringHashable(String string) {
        this.string = string;
    }

    @Override
    public String blockDataHash() {
        return string;
    }
}

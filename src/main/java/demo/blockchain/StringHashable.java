package demo.blockchain;

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

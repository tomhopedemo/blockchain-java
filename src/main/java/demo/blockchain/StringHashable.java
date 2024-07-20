package demo.blockchain;

public class StringHashable implements BlockHashable {

    String string;

    public StringHashable(String string) {
        this.string = string;
    }

    @Override
    public String blockhash() throws Exception {
        return string;
    }
}

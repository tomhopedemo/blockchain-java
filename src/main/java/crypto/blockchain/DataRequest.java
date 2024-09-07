package crypto.blockchain;

public record DataRequest (String data) implements Request {

    @Override
    public String getPreHash() {
        return data;
    }

}

package crypto.blockchain;

public record DataRequest (String data) implements Request, BlockDataHashable {

    @Override
    public String getBlockDataHash() {
        return data;
    }

}

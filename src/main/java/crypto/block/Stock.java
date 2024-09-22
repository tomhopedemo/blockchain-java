package crypto.block;

import crypto.BlockData;
import crypto.SimpleRequest;

public record Stock(String key, String id, String update) implements SimpleRequest<Stock> {

    @Override
    public void mine(String id, BlockData<Stock> b) {

    }

    @Override
    public String getPreHash() {
        return key + "~" + id + "~" + update;
    }
}

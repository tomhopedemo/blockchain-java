package crypto.block;

import crypto.BlockData;
import crypto.SimpleRequest;

public record Vote(String key, String referendumHash, String option) implements SimpleRequest<Vote> {
    @Override
    public void mine(String id, BlockData<Vote> b) {

    }

    @Override
    public String getPreHash() {
        return key + "~" + referendumHash + "~" + option;
    }
}

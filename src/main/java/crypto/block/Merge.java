package crypto.block;

import crypto.BlockData;
import crypto.SimpleRequest;

public record Merge(String branchId, String signature) implements SimpleRequest<Merge> {
    @Override
    public void mine(String id, BlockData<Merge> b) {

    }

    @Override
    public String getPreHash() {
        return "";
    }
}

package crypto.block;

import crypto.BlockData;
import crypto.SimpleRequest;

import java.util.List;

//name of refereundum, options...
public record Referendum(String key, String name, List<String> options, boolean allowFreeform) implements SimpleRequest<Referendum> {
    @Override
    public void mine(String id, BlockData<Referendum> b) {

    }

    @Override
    public String getPreHash() {
        return key + "~" + name + "~" + String.join("@", options) + "~" + allowFreeform;
    }
}

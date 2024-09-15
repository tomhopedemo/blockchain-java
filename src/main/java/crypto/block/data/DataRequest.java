package crypto.block.data;

import crypto.Request;

public record DataRequest (String data) implements Request {

    @Override
    public String getPreHash() {
        return data;
    }

}

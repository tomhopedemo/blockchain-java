package crypto.block;

import crypto.*;


import static java.nio.charset.StandardCharsets.UTF_8;

public record Data(String data, String format) implements SimpleRequest<Data> {

    public Data(byte[] data, String format) { this(new String(data, UTF_8), format);}

    public String getPreHash() { return data + "~" + format; }

    public void mine(String id, BlockData<Data> blockData) {
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), this.getClass());
    }

}

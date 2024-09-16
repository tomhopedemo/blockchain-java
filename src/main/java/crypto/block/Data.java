package crypto.block;

import crypto.*;

import java.util.List;

public record Data(String data) implements Request<Data> {

    @Override
    public String getPreHash() {
        return data;
    }


    @Override
    public void mine(String id, BlockData<Data> blockData) {
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), BlockType.DATA);
    }

    @Override
    public BlockData<Data> prepare(String id, List<Data> requests) {
        return requests.isEmpty() ? null : new BlockData<>(List.of(requests.getFirst()));
    }

    @Override
    public boolean verify(String id, Data request) {
        return true;
    }

}

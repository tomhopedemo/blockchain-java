package crypto.block.data;

import crypto.*;

import java.util.List;

public record DataRequest (String data) implements Request<DataRequest> {

    @Override
    public String getPreHash() {
        return data;
    }


    @Override
    public void mine(String id, BlockData<DataRequest> blockData) {
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), BlockType.DATA);
    }

    @Override
    public BlockData<DataRequest> prepare(String id, List<DataRequest> requests) {
        return requests.isEmpty() ? null : new BlockData<>(List.of(requests.getFirst()));
    }

    @Override
    public boolean verify(String id, DataRequest request) {
        return true;
    }

}

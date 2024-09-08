package crypto.block.data;

import crypto.*;
import crypto.BlockData;

import java.util.List;

public record DataFactory(String id) implements BlockFactory<DataRequest>{

    @Override
    public void mine(BlockData<DataRequest> blockData) {
        addBlock(id, blockData);
        Requests.remove(id, blockData.data(), BlockType.DATA);
    }

    @Override
    public BlockData<DataRequest> prepare(List<DataRequest> requests) {
        return requests.isEmpty() ? null : new BlockData<>(List.of(requests.getFirst()));
    }

    @Override
    public boolean verify(DataRequest request) {
        return true;
    }
}
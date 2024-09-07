package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.blockchain.BlockData;

import java.util.List;

public record SimpleBlockFactory(String id) implements BlockFactory<DataRequest>{

    @Override
    public void mine(BlockData<DataRequest> blockData) {
        Blockchain chain = Data.getChain(id);
        Block nextBlock = new Block(blockData, chain.getMostRecentHash());
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        chain.add(nextBlock);
        Requests.remove(id, blockData.data(), BlockType.DATA);
    }

    @Override
    public BlockData<DataRequest> prepare(List<DataRequest> requests) {
        if (requests.isEmpty()){
            return null;
        } else {
            return new BlockData<>(List.of(requests.getFirst()));
        }
    }

    @Override
    public boolean verify(DataRequest request) {
        return true;
    }
}
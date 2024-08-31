package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.blockchain.signed.BlockDataWrapper;

import java.util.List;

public record SimpleBlockFactory(String id) implements BlockFactory<BlockDataWrapper<DataRequest>, DataRequest>{

    @Override
    public void mineNextBlock(BlockDataWrapper<DataRequest> blockDataWrapper) {
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        Block nextBlock = new Block(blockDataWrapper, previousBlockHash);
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        chain.add(nextBlock);
        Requests.remove(id, blockDataWrapper.blockData(), BlockType.DATA);
    }

    @Override
    public BlockDataWrapper<DataRequest> prepareRequests(List<DataRequest> requests) {
        if (requests.isEmpty()){
            return null;
        } else {
            return new BlockDataWrapper<>(List.of(requests.getFirst()));
        }
    }
}
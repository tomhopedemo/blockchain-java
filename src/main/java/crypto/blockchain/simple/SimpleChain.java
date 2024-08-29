package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.blockchain.signed.BlockDataWrapper;
import crypto.blockchain.signed.SignedDataRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SimpleChain(String id) implements BlockFactory<BlockDataWrapper, DataRequest>{

    @Override
    public void mineNextBlock(BlockDataWrapper blockDataWrapper) {
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        Block nextBlock = new Block(blockDataWrapper, mostRecentBlock.getBlockHashId());
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        chain.add(nextBlock);
        List<DataRequest> requests = new ArrayList<>();
        for (BlockDataHashable blockDataHashable : blockDataWrapper.blockData()) {
            requests.add((DataRequest) blockDataHashable);
        }
        Requests.remove(id, requests, BlockType.DATA);

    }

    @Override
    public Optional<BlockDataWrapper> prepareRequests(List<DataRequest> requests) {
        if (requests.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new BlockDataWrapper(List.of(requests.getFirst())));
        }
    }
}
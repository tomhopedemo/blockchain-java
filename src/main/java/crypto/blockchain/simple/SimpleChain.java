package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.List;
import java.util.Optional;

public record SimpleChain(String id) {

    public void genesis(String value) {
        Blockchain chain = Data.getChain(id);
        Block block = new Block(new StringHashable(value), "");
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);
    }

    public Optional<List<DataRequest>> prepareRequests(List<DataRequest> requests) {
        if (requests.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(List.of(requests.getFirst()));
        }
    }

    public void mineNextBlock(List<DataRequest> dataRequests) {
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        StringHashable data = new StringHashable(dataRequests.getFirst().data());
        Block nextBlock = new Block(data, mostRecentBlock.blockHashId);
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        Requests.remove(id, dataRequests, BlockType.DATA);
        chain.add(nextBlock);
    }



}
package crypto.blockchain.simple;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public record SimpleChain(String id) {

    public void genesis() {
        Blockchain blockchain = Data.getChain(id);
        Block block = new Block(new StringHashable("abcde"), "");
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);
    }

    public void simulate() {
        Blockchain blockchain = Data.getChain(id);
        List<DataRequest> request = constructData();
        Block nextBlock = mineNextBlock(request);
        blockchain.add(nextBlock);
    }

    public Optional<List<DataRequest>> prepareRequests(List<DataRequest> requests) {
        if (requests.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(List.of(requests.getFirst()));
        }
    }

    public Block mineNextBlock(List<DataRequest> dataRequests) {
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        StringHashable data = new StringHashable(dataRequests.getFirst().data());
        Block nextBlock = new Block(data, mostRecentBlock.blockHashId);
        BlockMiner.mineBlockHash(nextBlock, "0".repeat(1));
        Requests.remove(id, dataRequests, BlockType.DATA);
        return nextBlock;
    }

    private List<DataRequest> constructData() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return List.of(new DataRequest(sb.toString()));
    }

}
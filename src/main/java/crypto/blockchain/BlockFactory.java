package crypto.blockchain;

import java.util.List;

public interface BlockFactory<R extends Request> {

    void mine(BlockData<R> b);

    BlockData<R> prepare(List<R> requests);

    boolean verify(R request);

    default boolean verify(BlockData<R> blockData){
        for (R request : blockData.data()) {
            if (!verify(request)) return false;
        }
        return true;
    }

    default void addBlock(String id, BlockData<R> blockData){
        Blockchain chain = Data.getChain(id);
        Block block = new Block(blockData, chain.getMostRecentHash());
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);
    }

}
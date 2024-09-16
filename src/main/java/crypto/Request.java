package crypto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface Request<R extends Request> extends BlockDataHashable {

    void mine(String id, BlockData<R> b);

    BlockData<R> prepare(String id, List<R> requests);

    boolean verify(String id, R request);

    default boolean verify(String id, BlockData<R> blockData){
        for (R request : blockData.data()) {
            if (!verify(id, request)) return false;
        }
        return true;
    }

    default void addBlock(String id, BlockData<R> blockData) {
        Blockchain chain = Caches.getChain(id);
        Block block = new Block(blockData, chain.getMostRecentHash());
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);
        try {
            writeBlock(id, chain.blocks.size() - 1, block);
        } catch (IOException ignored){}
    }

    Gson JSON = new GsonBuilder().setPrettyPrinting().create();

    default void writeBlock(String id, int index, Block block) throws IOException {
        String dir = "storage/" + id + "/";
        if (!Files.exists(Path.of(dir))) {
            Files.createDirectories(Path.of(dir));
        }
        Files.write(Path.of(dir + index + "-" + block.getBlockHashId()), JSON.toJson(block).getBytes());
    }



}

package crypto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import crypto.hashing.Hashing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Request<R extends Request> extends BlockDataHashable {

    Map<Class, Map<String, List<? extends Request>>> requestMaps = new HashMap<>();

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
        Hashing.Type hashType = Caches.getHashType(id);
        Block block = new Block(blockData, chain.getMostRecentHash(), hashType);
        BlockMiner.mineBlockHash(block, "0".repeat(1), hashType);
        chain.add(block);
        try {
            writeBlock(id, chain.blocks.size() - 1, block);
        } catch (IOException ignored){}
    }

    default void writeBlock(String id, int index, Block block) throws IOException {
        String dir = "storage/" + id + "/";
        if (!Files.exists(Path.of(dir))) {
            Files.createDirectories(Path.of(dir));
        }
        Gson JSON = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Path.of(dir + index + "-" + block.getBlockHashId()), JSON.toJson(block).getBytes());
    }

    static Map<String, List<? extends Request>> getRequestMap(Class<? extends Request> clazz){
        return requestMaps.get(clazz);
    }

}

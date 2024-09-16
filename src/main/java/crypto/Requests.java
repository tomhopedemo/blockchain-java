package crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    private static Map getMap(BlockType blockType) {
        return blockType.getRequestMap();
    }

    public static void add(String id, Request request) {
        BlockType blockType = BlockType.getType(request.getClass());
        Map requests = getMap(blockType);
        ((List) requests.computeIfAbsent(id, _ -> new ArrayList<>())).add(request);
    }

    public static List<? extends Request> get(String id, BlockType blockType) {
        return (List) getMap(blockType).get(id);
    }

    public static void remove(String id, List<? extends Request> requests, BlockType blockType) {
        List<? extends Request> found = get(id, blockType);
        if (found != null) found.removeAll(requests);
    }

}

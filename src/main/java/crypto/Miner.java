package crypto;

import java.util.List;

public record Miner (String id) implements Runnable {

    @Override
    public void run() {
        try {
            runSynch();
        } finally {
            MinerPool.removeMiner(id);
        }
    }

    public void runSynch()  {
        for (BlockType blockType : Caches.getTypes(id)) {
            List<? extends Request> requests = Requests.get(id, blockType);
            if (requests == null || requests.isEmpty()) continue;
            try {
                BlockData blockDataHashable = requests.getFirst().prepare(id, requests);
                if (blockDataHashable == null) continue;
                requests.getFirst().mine(id, blockDataHashable);
            } catch (Exception ignored) {}
        }
    }
}

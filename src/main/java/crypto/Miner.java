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
        for (BlockType blockType : Data.getTypes(id)) {
            List<? extends Request> requests = Requests.get(id, blockType);
            if (requests == null || requests.isEmpty()) continue;
            try {
                BlockFactory blockFactory = blockType.getFactoryClass().getDeclaredConstructor(String.class).newInstance(id);
                BlockData blockDataHashable = blockFactory.prepare(requests);
                if (blockDataHashable == null) continue;
                blockFactory.mine(blockDataHashable);
            } catch (Exception ignored) {}
        }
    }
}

package crypto.block;

import crypto.*;

public record Publish(String publicKey) implements SimpleRequest<Publish> {

    @Override
    public void mine(String id, BlockData<Publish> blockData) {
        if (blockData.data().size() != 1) return;
        if (Caches.isPublished(id)) return;
        Publish first = blockData.data().getFirst();
        if (!verify(id, first)) return;
        addBlock(id, blockData);
        Caches.publish(id);
        Requests.remove(id, blockData.data(), BlockType.PUBLISH);
    }

    @Override
    public String getPreHash() {
        return "publish";
    }
}

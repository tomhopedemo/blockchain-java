package crypto.block;

import crypto.*;

//this could be generalized to a flag mechanism. //also will require signature.
//cleaner mechansim for blocks with a single instance.
public record Publish(String publicKey) implements SimpleRequest<Publish> {

    @Override
    public void mine(String id, BlockData<Publish> blockData) {
        if (blockData.data().size() != 1) return;
        if (Caches.isPublished(id)) return;
        Publish first = blockData.data().getFirst();
        if (!verify(id, first)) return;
        addBlock(id, blockData);
        Caches.publish(id);
        Requests.remove(id, blockData.data(), this.getClass());
    }

    @Override
    public String getPreHash() {
        return "publish";
    }
}

package crypto.block;

import crypto.*;

public record Difficulty(Integer difficulty, String currency, String publicKey) implements SimpleRequest<Difficulty> {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency + "~" + difficulty;
    }

    @Override
    public void mine(String id, BlockData<Difficulty> blockData) {
        for (Difficulty request : blockData.data()) {
            if (!Caches.hasKey(id, request.publicKey())) return; //i.e. this is a block anyone can submit - so maybe shouldn't have a key?
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Caches.addDifficulty(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
    }

}

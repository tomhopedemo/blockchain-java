package crypto.block;

import crypto.*;

import java.util.ArrayList;
import java.util.List;

public record Difficulty(Integer difficulty, String currency, String publicKey) implements Request<Difficulty> {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency + "~" + difficulty;
    }

    public BlockData<Difficulty> prepare(String id, List<Difficulty> requests){
        return new BlockData<>(new ArrayList<>(requests));
    }

    public boolean verify(String id, Difficulty request){
        return true;
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

package crypto.block.difficulty;

import crypto.*;
import org.apache.commons.lang3.builder.Diff;

import java.util.ArrayList;
import java.util.List;

public record DifficultyRequest(Integer difficulty, String currency, String publicKey) implements Request<DifficultyRequest> {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency + "~" + difficulty;
    }

    public BlockData<DifficultyRequest> prepare(String id, List<DifficultyRequest> requests){
        return new BlockData<>(new ArrayList<>(requests));
    }

    public boolean verify(String id, DifficultyRequest request){
        return true;
    }

    @Override
    public void mine(String id, BlockData<DifficultyRequest> blockData) {
        for (DifficultyRequest request : blockData.data()) {
            if (!Data.hasKey(id, request.publicKey())) return; //i.e. this is a block anyone can submit - so maybe shouldn't have a key?
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addDifficulty(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
    }

}

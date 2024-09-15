package crypto.block.difficulty;

import crypto.*;

import java.util.ArrayList;
import java.util.List;

public record DifficultyFactory(String id) implements BlockFactory<DifficultyRequest> {

    @Override
    public void mine(BlockData<DifficultyRequest> blockData) {
        for (DifficultyRequest request : blockData.data()) {
            if (!Data.hasKey(id, request.publicKey())) return; //i.e. this is a block anyone can submit - so maybe shouldn't have a key?
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addDifficulty(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
    }

    @Override
    public BlockData<DifficultyRequest> prepare(List<DifficultyRequest> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(DifficultyRequest request) {
        return true;
    }


}

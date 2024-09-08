package crypto.block.keypair;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;

import java.util.ArrayList;
import java.util.List;

public record KeypairFactory(String id) implements BlockFactory<Keypair> {

    @Override
    public void mine(BlockData<Keypair> blockData) {
        for (Keypair request : blockData.data()) {
            if (!ECDSA.checkKeypair(request)) return;
            if (Data.hasKey(id, request.publicKey())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addKeypair(id, request));
        Requests.remove(id, blockData.data(), BlockType.KEYPAIR);
    }

    @Override
    public BlockData<Keypair> prepare(List<Keypair> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(Keypair request) {
        return true;
    }
}

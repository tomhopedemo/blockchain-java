package crypto.block.keypair;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;

import java.util.ArrayList;
import java.util.List;

public record KeyPairFactory(String id) implements BlockFactory<KeyPair> {

    @Override
    public void mine(BlockData<KeyPair> blockData) {
        for (KeyPair request : blockData.data()) {
            if (!ECDSA.checkKeyPair(request.publicKey(), request.privateKey())) return;
            if (Data.hasKey(id, request.publicKey())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addKeyPair(id, request));
        Requests.remove(id, blockData.data(), BlockType.KEYPAIR);
    }

    @Override
    public BlockData<KeyPair> prepare(List<KeyPair> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(KeyPair request) {
        return true;
    }
}

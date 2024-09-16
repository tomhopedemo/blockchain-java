package crypto;

import crypto.block.difficulty.DifficultyRequest;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public record Keypair(String privateKey, String publicKey) implements Request<Keypair> {

    private Keypair(PrivateKey privateKey, PublicKey publicKey) {
        this(Encoder.encodeToString(privateKey), Encoder.encodeToString(publicKey));
    }

    public static Keypair create() {
        KeyPair keyPair = ECDSA.generateKeyPair();
        return new Keypair(keyPair.getPrivate(), keyPair.getPublic());
    }

    @Override
    public String getPreHash() {
        return privateKey + "~" + publicKey;
    }

    @Override
    public void mine(String id, BlockData<Keypair> blockData) {
        for (Keypair request : blockData.data()) {
            if (!ECDSA.checkKeypair(request)) return;
            if (Data.hasKey(id, request.publicKey())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addKeypair(id, request));
        Requests.remove(id, blockData.data(), BlockType.KEYPAIR);
    }

    public BlockData<Keypair> prepare(String id, List<Keypair> requests){
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(String id, Keypair request) {
        return true;
    }

}

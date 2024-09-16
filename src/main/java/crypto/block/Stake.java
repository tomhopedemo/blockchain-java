package crypto.block;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.ArrayList;
import java.util.List;

public record Stake(String publicKey, String currency, long value, int index, String signature) implements Request<Stake> {

    @Override
    public String getPreHash() {
        return signature;
    }

    public static String generateHash(String publicKey, String currency, long value, int index) {
        String preHash = publicKey + "~" + currency + "~" + value + "~" + index;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    static int BLOCK_EXPIRY = 10;
    static int SIZE = 3;

    @Override
    public void mine(String id, BlockData<Stake> blockData) {
        if (!verify(id, blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        int size = Caches.getChain(id).blocks.size();
        for (Stake request : blockData.data()) {
            Caches.addStake(id, request.currency(), request.publicKey(), request.value(), size + BLOCK_EXPIRY);
        }
        Requests.remove(id, blockData.data(), BlockType.STAKE);
    }

    @Override
    public BlockData<Stake> prepare(String id, List<Stake> requests) {
        List<Stake> selected = new ArrayList<>();
        for (Stake request : requests) {
            if (!verify(id, request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(String id, Stake request) {
        return true;
    }

    public static Request create(Keypair keypair, String currency) throws ChainException {
        String hash = Stake.generateHash(keypair.publicKey(), currency, SIZE, BLOCK_EXPIRY);
        byte[] signature = Signing.sign(keypair, hash);
        return new Stake(keypair.publicKey(), currency, SIZE, BLOCK_EXPIRY,  Encoder.encodeToHexadecimal(signature));
    }

}

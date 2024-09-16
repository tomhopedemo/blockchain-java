package crypto.block.stake;

import crypto.*;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.ArrayList;
import java.util.List;

public record StakeRequest(String publicKey, String currency, long value, int index, String signature) implements Request<StakeRequest> {

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
    public void mine(String id, BlockData<StakeRequest> blockData) {
        if (!verify(id, blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        int size = Data.getChain(id).blocks.size();
        for (StakeRequest request : blockData.data()) {
            Data.addStake(id, request.currency(), request.publicKey(), request.value(), size + BLOCK_EXPIRY);
        }
        Requests.remove(id, blockData.data(), BlockType.STAKE);
    }

    @Override
    public BlockData<StakeRequest> prepare(String id, List<StakeRequest> requests) {
        List<StakeRequest> selected = new ArrayList<>();
        for (StakeRequest request : requests) {
            if (!verify(id, request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(String id, StakeRequest request) {
        return true;
    }

    public static Request create(Keypair keypair, String currency) throws ChainException {
        String hash = StakeRequest.generateHash(keypair.publicKey(), currency, SIZE, BLOCK_EXPIRY);
        byte[] signature = Signing.sign(keypair, hash);
        return new StakeRequest(keypair.publicKey(), currency, SIZE, BLOCK_EXPIRY,  Encoder.encodeToHexadecimal(signature));
    }

}

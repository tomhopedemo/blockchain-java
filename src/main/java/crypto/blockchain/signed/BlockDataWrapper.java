package crypto.blockchain.signed;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class BlockDataWrapper implements BlockDataHashable {

    List<BlockDataHashable> blockData;

    public BlockDataWrapper(List<BlockDataHashable> transactionRequests) {
        this.blockData = transactionRequests;
    }

    public List<BlockDataHashable> getBlockData() {
        return blockData;
    }

    @Override
    public String blockDataHash()  {
        String preHash = String.join("", blockData.stream().map(transactionRequest -> transactionRequest.blockDataHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

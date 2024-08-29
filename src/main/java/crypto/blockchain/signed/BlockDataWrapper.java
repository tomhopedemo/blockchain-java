package crypto.blockchain.signed;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class BlockDataWrapper implements BlockDataHashable {

    List<? extends BlockDataHashable> blockData;

    public BlockDataWrapper(List<? extends BlockDataHashable> transactionRequests) {
        this.blockData = transactionRequests;
    }

    public List<? extends BlockDataHashable> getBlockData() {
        return blockData;
    }

    @Override
    public String getBlockDataHash()  {
        String preHash = String.join("", blockData.stream().map(transactionRequest -> transactionRequest.getBlockDataHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

}

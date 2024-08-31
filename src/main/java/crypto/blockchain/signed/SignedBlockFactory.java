package crypto.blockchain.signed;

import crypto.blockchain.*;
import java.util.ArrayList;
import java.util.List;

public record SignedBlockFactory(String id) implements BlockFactory<BlockDataWrapper<SignedDataRequest>, SignedDataRequest>{

    @Override
    public void mineNextBlock(BlockDataWrapper<SignedDataRequest> requests) {
        //Data Request Verification
        for (SignedDataRequest signedDataRequest : requests.blockData()) {
            boolean verified = SignedDataRequestVerification.verifySignature(signedDataRequest);
            if (!verified) {
                return;
            }
        }

        //Create block
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);

        //Update caches
        Requests.remove(id, requests.blockData(), BlockType.SIGNED_DATA);
    }

    @Override
    public BlockDataWrapper prepareRequests(List<SignedDataRequest> requests) {
        return new BlockDataWrapper(new ArrayList<>(requests));
    }
}

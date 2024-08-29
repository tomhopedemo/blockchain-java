package crypto.blockchain.signed;

import crypto.blockchain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 'Transaction' is a single SignedDataRequest
 */
public record SignedChain (String id) {

    public void mineNextBlock(BlockDataWrapper requests) {
        //Data Request Verification
        for (BlockDataHashable blockData : requests.getBlockData()) {
            SignedDataRequest signedDataRequest = (SignedDataRequest) blockData;
            boolean verified = SignedChainVerification.verifySignature(signedDataRequest);
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
        List<SignedDataRequest> signedDataRequests = new ArrayList<>();
        for (BlockDataHashable blockDataHashable : requests.getBlockData()) {
            signedDataRequests.add((SignedDataRequest) blockDataHashable);
        }
        Requests.remove(id, signedDataRequests, BlockType.SIGNED_DATA);
    }


    public Optional<BlockDataWrapper> prepareRequests(List<SignedDataRequest> requests) {
        return Optional.of(new BlockDataWrapper(requests));
    }
}

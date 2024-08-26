package crypto.blockchain.signed;

import crypto.blockchain.*;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequests;
import crypto.blockchain.account.AccountTransactionVerification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * 'Transaction' is a single SignedDataRequest
 */
public record SignedChain (String id) {

    public void genesis(String value, String key) {
        BlockDataWrapper requests = new BlockDataWrapper(List.of(new SignedDataRequest(key, value)));
        mineNextBlock(requests);
    }

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


}

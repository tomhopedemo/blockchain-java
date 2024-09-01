package crypto.blockchain;

import crypto.blockchain.signed.BlockDataWrapper;
import crypto.blockchain.signed.SignedDataRequestVerification;
import crypto.cryptography.ECDSA;

import java.util.ArrayList;
import java.util.List;


public record CurrencyBlockFactory(String id) implements BlockFactory<BlockDataWrapper<CurrencyRequest>, CurrencyRequest> {

    @Override
    public void mineNextBlock(BlockDataWrapper<CurrencyRequest> requests) {
        for (CurrencyRequest request : requests.blockData()) {
            boolean valid = ECDSA.checkKeyPair(request.publicKey(), request.privateKey());
            if (!valid){
                return;
            }
            if (Data.hasCurrency(id, request.currency())){
                return;
            }
        }

        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);

        for (CurrencyRequest request : requests.blockData()) {
            Data.addCurrency(id, request);
        }

        Requests.remove(id, requests.blockData(), BlockType.CURRENCY);
    }

    @Override
    public BlockDataWrapper<CurrencyRequest> prepareRequests(List<CurrencyRequest> requests) {
        return new BlockDataWrapper<>(new ArrayList<>(requests));
    }
}

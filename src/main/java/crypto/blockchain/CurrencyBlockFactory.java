package crypto.blockchain;

import crypto.cryptography.ECDSA;

import java.util.ArrayList;
import java.util.List;


public record CurrencyBlockFactory(String id) implements BlockFactory<CurrencyRequest> {

    @Override
    public void mine(BlockData<CurrencyRequest> requests) {
        for (CurrencyRequest request : requests.data()) {
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

        for (CurrencyRequest request : requests.data()) {
            Data.addCurrency(id, request);
        }

        Requests.remove(id, requests.data(), BlockType.CURRENCY);
    }

    @Override
    public BlockData<CurrencyRequest> prepare(List<CurrencyRequest> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(CurrencyRequest request) {
        return true;
    }


}

package crypto.block;

import crypto.*;

import java.util.ArrayList;
import java.util.List;


public record Currency(String currency, String publicKey) implements Request<Currency> {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency;
    }


    @Override
    public void mine(String id, BlockData<Currency> blockData) {
        for (Currency request : blockData.data()) {
            if (!Caches.hasKey(id, request.publicKey())) return;
            if (Caches.hasCurrency(id, request.currency())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Caches.addCurrency(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
    }

    @Override
    public BlockData<Currency> prepare(String id, List<Currency> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(String id, Currency request) {
        return true;
    }

}

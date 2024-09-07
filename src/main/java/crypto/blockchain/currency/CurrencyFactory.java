package crypto.blockchain.currency;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;

import java.util.ArrayList;
import java.util.List;

public record CurrencyFactory(String id) implements BlockFactory<CurrencyRequest> {

    @Override
    public void mine(BlockData<CurrencyRequest> blockData) {
        for (CurrencyRequest request : blockData.data()) {
            if (!Data.hasKey(id, request.publicKey())) return;
            if (Data.hasCurrency(id, request.currency())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addCurrency(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
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

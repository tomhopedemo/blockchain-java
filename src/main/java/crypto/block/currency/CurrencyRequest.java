package crypto.block.currency;

import crypto.*;

import java.util.ArrayList;
import java.util.List;


public record CurrencyRequest(String currency, String publicKey) implements Request<CurrencyRequest> {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency;
    }


    @Override
    public void mine(String id, BlockData<CurrencyRequest> blockData) {
        for (CurrencyRequest request : blockData.data()) {
            if (!Data.hasKey(id, request.publicKey())) return;
            if (Data.hasCurrency(id, request.currency())) return;
        }
        addBlock(id, blockData);
        blockData.data().forEach(request -> Data.addCurrency(id, request));
        Requests.remove(id, blockData.data(), BlockType.CURRENCY);
    }

    @Override
    public BlockData<CurrencyRequest> prepare(String id, List<CurrencyRequest> requests) {
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    public boolean verify(String id, CurrencyRequest request) {
        return true;
    }

}

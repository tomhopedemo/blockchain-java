package crypto.block.currency;

import crypto.Request;


public record CurrencyRequest(String currency, String publicKey) implements Request {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency;
    }

}

package crypto.caches;

import crypto.block.Currency;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class CurrencyCache {

    private final Set<Currency> currencies = new LinkedHashSet<>();

    public void add(Currency currencyData){
        Optional<Currency> found = get(currencyData.currency());
        if (found.isEmpty()){
            currencies.add(currencyData);
        }
    }

    public Optional<Currency> get(String currency) {
        return currencies.stream().filter(c -> currency.equals(c.currency())).findFirst();
    }

}

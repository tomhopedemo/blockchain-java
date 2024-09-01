package crypto.blockchain;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class CurrencyCache {

    private final Set<CurrencyRequest> currencies = new LinkedHashSet<>();

    public void add(CurrencyRequest currencyData){
        Optional<CurrencyRequest> found = get(currencyData.currency());
        if (found.isEmpty()){
            currencies.add(currencyData);
        }
    }

    public Optional<CurrencyRequest> get(String currency) {
        return currencies.stream().filter(c -> currency.equals(c.currency())).findFirst();
    }

}

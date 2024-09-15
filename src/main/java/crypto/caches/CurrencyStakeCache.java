package crypto.caches;

import crypto.Stake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyStakeCache {

    private final ConcurrentHashMap<String, StakeCache> currencyStakeCache = new ConcurrentHashMap<>();

    public void add(String publicKey, String currency, Long value, int expiry){
        currencyStakeCache.computeIfAbsent(currency, _ -> new StakeCache()).add(publicKey, value, expiry);
    }

    public boolean hasCurrency(String currency) {
        return currencyStakeCache.get(currency) != null;
    }

    public static class StakeCache {
        public List<Stake> stakes = new ArrayList<>();

        public void add(String publicKey, Long value, int expiry){
            stakes.add(new Stake(publicKey, value, expiry));
        }

        public Stake get(String publicKey) {
            Optional<Stake> first = stakes.stream().filter(s -> publicKey.equals(s.publicKey())).findFirst();
            return first.isPresent() ? first.get() : null;
        }
    }
}

package crypto.caches;

import crypto.StakeCacheItem;

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
        public List<StakeCacheItem> stakes = new ArrayList<>();

        public void add(String publicKey, Long value, int expiry){
            stakes.add(new StakeCacheItem(publicKey, value, expiry));
        }

        public StakeCacheItem get(String publicKey) {
            Optional<StakeCacheItem> first = stakes.stream().filter(s -> publicKey.equals(s.publicKey())).findFirst();
            return first.isPresent() ? first.get() : null;
        }
    }
}

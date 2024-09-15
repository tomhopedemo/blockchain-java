package crypto.caches;

import java.util.concurrent.ConcurrentHashMap;

public class CurrencyAccountCache {

    private final ConcurrentHashMap<String, AccountCache> currencyAccountCache = new ConcurrentHashMap<>();

    public void add(String publicKey, String currency, Long value){
        currencyAccountCache.computeIfAbsent(currency, _ -> new AccountCache()).add(publicKey, value);
    }

    public Long getBalance(String publicKey, String currency) {
        AccountCache accountCache = currencyAccountCache.get(currency);
        if (accountCache == null){
            return 0L;
        }
        Long value = accountCache.get(publicKey);
        return value == null ? 0L : value;
    }

    public boolean hasCurrency(String currency) {
        return currencyAccountCache.get(currency) != null;
    }

    public static class AccountCache {
        public ConcurrentHashMap<String, Long> accounts = new ConcurrentHashMap<>();

        public void add(String publicKey, Long value){
            accounts.compute(publicKey, (_, v) -> v == null ?  value : v + value);
        }

        public Long get(String publicKey) {
            return accounts.get(publicKey);
        }
    }
}

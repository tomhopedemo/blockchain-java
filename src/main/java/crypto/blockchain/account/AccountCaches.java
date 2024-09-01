package crypto.blockchain.account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountCaches {

    private final ConcurrentHashMap<String, AccountCache> accountCaches = new ConcurrentHashMap<>();

    public AccountCache get(String currency) {
        return accountCaches.get(currency);
    }

    public void add(String publicKey, String currency, Long value){
        accountCaches.computeIfAbsent(currency, _ -> new AccountCache()).add(publicKey, value);
    }

    public Long get(String publicKey, String currency) {
        AccountCache accountCache = get(currency);
        if (accountCache == null){
            return 0L;
        }
        Long value = accountCache.get(publicKey);
        return value == null ? 0L : value;
    }

    public boolean hasCurrency(String currency) {
        return accountCaches.get(currency) != null;
    }
}

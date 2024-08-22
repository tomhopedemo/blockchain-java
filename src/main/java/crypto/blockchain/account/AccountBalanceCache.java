package crypto.blockchain.account;

import java.util.concurrent.ConcurrentHashMap;

public class AccountBalanceCache {

    public ConcurrentHashMap<String, Long> accounts = new ConcurrentHashMap<>();

    public void add(String accountPublicKey, Long value){
        accounts.compute(accountPublicKey, (_, v) -> v == null ?  value : v + value);
    }

    public void subtract(String accountPublicKey, Long value){
        accounts.compute(accountPublicKey, (_, v) -> v == null ? -value : v - value);
    }

    public Long get(String publicKeyAddress) {
        return accounts.get(publicKeyAddress);
    }
}

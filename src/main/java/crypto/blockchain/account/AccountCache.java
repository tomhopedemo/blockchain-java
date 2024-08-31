package crypto.blockchain.account;

import java.util.concurrent.ConcurrentHashMap;

public class AccountCache {

    public ConcurrentHashMap<String, Long> accounts = new ConcurrentHashMap<>();

    public void add(String publicKey, Long value){
        accounts.compute(publicKey, (_, v) -> v == null ?  value : v + value);
    }

    public Long get(String publicKey) {
        return accounts.get(publicKey);
    }
}

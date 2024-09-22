package crypto.caches;

public record StakeCacheItem(String publicKey, Long value, int expiry) {
}

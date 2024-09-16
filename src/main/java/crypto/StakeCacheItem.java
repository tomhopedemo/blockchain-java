package crypto;

public record StakeCacheItem(String publicKey, Long value, int expiry) {
}

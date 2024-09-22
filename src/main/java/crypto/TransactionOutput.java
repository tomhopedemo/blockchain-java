package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public record TransactionOutput (String recipient, String currency, String value) {

    public TransactionOutput(String recipientPublicKey, String currency, long value) {
        this(recipientPublicKey, currency, String.valueOf(value));
    }

    public long getValue(){
        return Long.parseLong(value);
    }

    public String getRecipient(){
        return recipient;
    }

    public String generateTransactionOutputHash(String transactionRequestHash, Hashing.Type hashtype) {
        String preHash = recipient + currency + transactionRequestHash + value;

        byte[] hash = Hashing.hash(preHash, hashtype);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String serialise(){
        return recipient + currency + value;
    }

}


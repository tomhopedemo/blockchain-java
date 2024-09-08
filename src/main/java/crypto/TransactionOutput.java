package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public record TransactionOutput (String recipient, String value) {

    public TransactionOutput(String recipientPublicKey, long value) {
        this(recipientPublicKey, String.valueOf(value));
    }

    public long getValue(){
        return Long.parseLong(value);
    }

    public String getRecipient(){
        return recipient;
    }

    public String generateTransactionOutputHash(String transactionRequestHash) {
        String preHash = recipient + transactionRequestHash + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String serialise(){
        return this.recipient + this.value;
    }

}


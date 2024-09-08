package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public class TransactionOutput {
    String recipient;
    String value;

    public TransactionOutput(String recipientPublicKey, long value) {
        this.recipient = recipientPublicKey;
        this.value = String.valueOf(value);
    }

    public TransactionOutput(String publicKey, String value) {
        this.recipient = publicKey;
        this.value = String.valueOf(value);
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


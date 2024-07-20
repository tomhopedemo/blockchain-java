package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

public class TransactionOutput {
    String recipient;
    String value;

    public TransactionOutput(String recipientPublicKeyAddress, long value) {
        this.recipient = recipientPublicKeyAddress;
        this.value = String.valueOf(value);
    }

    public long getValue(){
        return Long.parseLong(value);
    }

    public String getRecipient(){
        return recipient;
    }

    public String generateTransactionOutputHash(String transactionRequestHash) throws Exception {
        String preHash = recipient + transactionRequestHash + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String serialise(){
        return this.recipient + this.value;
    }

}


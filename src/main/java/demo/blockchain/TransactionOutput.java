package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.security.PublicKey;

public class TransactionOutput {
    String recipient;
    long value;

    public TransactionOutput(String recipientPublicKeyAddress, long value) {
        this.recipient = recipientPublicKeyAddress;
        this.value = value;
    }

    public long getValue(){
        return value;
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


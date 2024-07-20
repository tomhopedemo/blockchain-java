package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.security.PublicKey;

public class TransactionOutput {
    PublicKey recipient;
    long value;

    public TransactionOutput(PublicKey recipient, long value) {
        this.recipient = recipient;
        this.value = value;
    }

    public long getValue(){
        return value;
    }

    public PublicKey getRecipient(){
        return recipient;
    }

    public String generateTransactionOutputHash(String transactionRequestHash) throws Exception {
        String preHash = Encoder.encode(recipient) + transactionRequestHash + value;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public record TransactionOutputData (
            String recipientPublicKeyAddressEncoded,
            String value
    ){}

    public String serialise(){
        TransactionOutputData transactionOutputData = new TransactionOutputData(Encoder.encode(this.recipient), String.valueOf(this.value));
        return transactionOutputData.recipientPublicKeyAddressEncoded() + transactionOutputData.value();
    }

    //deserialise

}


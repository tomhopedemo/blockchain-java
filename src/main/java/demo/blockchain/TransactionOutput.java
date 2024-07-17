package demo.blockchain;

import java.security.PublicKey;

public class TransactionOutput {
    public PublicKey recipient;
    public long value;
    public String id;

    public TransactionOutput(String id, PublicKey recipient, long value) {
        this.recipient = recipient;
        this.value = value;
        this.id = id;
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

}


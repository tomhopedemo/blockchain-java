package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.security.PublicKey;
import java.util.List;


//transaction hash id to be encoded.
//the visualization of wallets to be the json mechanism -
//wallet private key as string and back again

public class TransactionRequest implements BlockHashable {

    public String senderPublicKeyAddress;
    public String recipientPublicKeyAddress;
    public long transactionValue;
    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;
    public String transactionRequestHashHex;

    public TransactionRequest(String senderPublicKeyAddress, String recipientPublicKeyAddress, long transactionValue, List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) throws Exception {
        this.senderPublicKeyAddress = senderPublicKeyAddress;
        this.recipientPublicKeyAddress = recipientPublicKeyAddress;
        this.transactionValue = transactionValue;
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
        this.transactionRequestHashHex = Encoder.encodeToHexadecimal(calculateTransactionHash());
    }

    private byte[] calculateTransactionHash() throws Exception {
        String preHash = senderPublicKeyAddress +
                recipientPublicKeyAddress +
                transactionValue +
                String.join("", getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()) +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());

        return Hashing.hash(preHash);
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

    public String getTransactionRequestHash() {
        return this.transactionRequestHashHex;
    }

    @Override
    public String blockhash() {
        return transactionRequestHashHex;
    }
}

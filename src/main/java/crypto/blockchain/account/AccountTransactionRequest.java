package crypto.blockchain.account;

import crypto.blockchain.BlockDataHashable;
import crypto.blockchain.Request;
import crypto.blockchain.TransactionOutput;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class AccountTransactionRequest implements BlockDataHashable, Request {

    public String publicKeyAddress;
    public List<TransactionOutput> transactionOutputs;
    public String transactionRequestHashHex;
    public String signature;

    public AccountTransactionRequest(String publicKeyAddress, List<TransactionOutput> transactionOutputs) {
        this.publicKeyAddress = publicKeyAddress;
        this.transactionOutputs = transactionOutputs;
        this.transactionRequestHashHex = Encoder.encodeToHexadecimal(calculateTransactionHash());
    }

    private byte[] calculateTransactionHash() {
        String preHash = publicKeyAddress +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        return Hashing.hash(preHash);
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public String generateTransactionOutputsHash() {
        String transactionRequestHash = getTransactionRequestHash();
        List<String> transactionOutputHashes = transactionOutputs.stream().map(output -> output.generateTransactionOutputHash(transactionRequestHash)).toList();
        String preHash = String.join("",transactionOutputHashes);
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String getTransactionRequestHash() {
        return this.transactionRequestHashHex;
    }

    @Override
    public String blockDataHash() {
        return transactionRequestHashHex;
    }

    public String getSignature(){
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = Encoder.encodeToHexadecimal(signature);
    }

    public String getPublicKeyAddress() {
        return publicKeyAddress;
    }
}

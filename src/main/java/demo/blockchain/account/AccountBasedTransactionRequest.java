package demo.blockchain.account;

import demo.blockchain.BlockDataHashable;
import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.util.List;

public class AccountBasedTransactionRequest implements BlockDataHashable {

    public String publicKeyAddress;
    public List<AccountBasedTransactionOutput> transactionOutputs;
    public String transactionRequestHashHex;
    public String signature;

    public AccountBasedTransactionRequest(String publicKeyAddress, List<AccountBasedTransactionOutput> transactionOutputs) {
        this.publicKeyAddress = publicKeyAddress;
        this.transactionOutputs = transactionOutputs;
        this.transactionRequestHashHex = Encoder.encodeToHexadecimal(calculateTransactionHash());
    }

    private byte[] calculateTransactionHash() {
        String preHash = publicKeyAddress +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        return Hashing.hash(preHash);
    }

    public List<AccountBasedTransactionOutput> getTransactionOutputs() {
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

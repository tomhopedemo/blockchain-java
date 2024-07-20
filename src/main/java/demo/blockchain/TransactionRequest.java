package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class TransactionRequest {

    public PublicKey senderPublicKeyAddress;
    public PublicKey recipientPublicKeyAddress;
    public float transactionValue;
    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;
    public byte[] transactionHashId;

    public TransactionRequest(Wallet senderWallet, PublicKey recipientPublicKeyAddress, float transactionValue, List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) throws Exception {
        this.senderPublicKeyAddress = senderWallet.publicKeyAddress;
        this.recipientPublicKeyAddress = recipientPublicKeyAddress;
        this.transactionValue = transactionValue;
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
        this.transactionHashId = calculateTransactionHash();
    }

    private byte[] calculateTransactionHash() throws Exception {
        String preHash = getPreHash();
        return Hashing.hash(preHash);
    }

    public String getPreHash(){
        return Encoder.encode(senderPublicKeyAddress) +
                Encoder.encode(recipientPublicKeyAddress) +
                transactionValue +
                transactionInputs + //convert to string.
                transactionOutputs;
    }

    public void addTransactionOutput(TransactionOutput transactionOutput) {
        transactionOutputs.add(transactionOutput);
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }

    public byte[] getHash() {
        return this.transactionHashId;
    }
}


//it's not about verifying the transaction, it's about verifying the indivial aspects so i don't think this is requied.
//maybe we could do a simple transaction mechanism also alongside this - i.e. a signle input/output.
//    public void calculateTransactionRequestSignature(PrivateKey privateKey) throws Exception {
//        byte[] presignature = getTransactionRequestPresignature();
//        signature = ECDSA.calculateECDSASignature(privateKey, presignature);
//    }
//
//
//
//    public boolean verifyTransactionRequestSignature() throws Exception {
//        byte[] presignature = getTransactionRequestPresignature();
//        return ECDSA.verifyECDSASignature(senderPublicKeyAddress, presignature, signature);
//    }
//
//    private byte[] getTransactionRequestPresignature(){
//        String signatureInputString = Encoder.encode(senderPublicKeyAddress) +
//                Encoder.encode(recipientPublicKeyAddress) +
//                transactionValue;
//        return signatureInputString.getBytes();
//    }

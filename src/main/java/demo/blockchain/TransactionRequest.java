package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class TransactionRequest {

    public PublicKey senderPublicKeyAddress;
    public PublicKey recipientPublicKeyAddress;
    public float transactionValue;

    public byte[] signature;

    public List<String> inputTransactionOutputIds; //we should start with a single input transaction and generalize.

    public TransactionRequest(Wallet senderWallet, PublicKey recipientPublicKeyAddress, float transactionValue, List<String> inputTransactionOutputIds) throws Exception {
        this.senderPublicKeyAddress = senderWallet.publicKeyAddress;
        this.recipientPublicKeyAddress = recipientPublicKeyAddress;
        this.transactionValue = transactionValue;
        this.inputTransactionOutputIds = inputTransactionOutputIds;
        generateSignature(senderWallet.privateKey);
    }

    public void generateSignature(PrivateKey privateKey) throws Exception {
        String data = Encoder.encode(senderPublicKeyAddress) + Encoder.encode(recipientPublicKeyAddress) + transactionValue;
        signature = ECDSA.calculateECDSASignature(privateKey, data);
    }

    public boolean verifySignature() {
        String data = Encoder.encode(senderPublicKeyAddress) + Encoder.encode(recipientPublicKeyAddress) + transactionValue;
        return ECDSA.verifyECDSASignature(senderPublicKeyAddress, data, signature);
    }

}
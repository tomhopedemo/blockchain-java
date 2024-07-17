package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class TransactionRequest {

    public PublicKey senderPublicKeyAddress;
    public PublicKey reciepientPublicKeyAddress;
    public float value;
    public byte[] signature;

    public List<String> inputTransactionIds; //inputTransactionIds

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    public TransactionRequest(Wallet senderWallet, PublicKey reciepientPublicKeyAddress, float transactionValue, List<String> inputTransactionIds) throws Exception {
        this.senderPublicKeyAddress = senderWallet.publicKeyAddress;
        this.reciepientPublicKeyAddress = reciepientPublicKeyAddress;
        this.value = transactionValue;
        this.inputTransactionIds = inputTransactionIds;
        generateSignature(senderWallet.privateKey);
    }

    public String calulateHash() throws Exception {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash - we'll instead encode the inputs.
        return ECDSA.applySha256HexadecimalEncoding(
                Encoder.encode(senderPublicKeyAddress) + Encoder.encode(reciepientPublicKeyAddress) + value + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) throws Exception {
        String data = Encoder.encode(senderPublicKeyAddress) + Encoder.encode(reciepientPublicKeyAddress) + value;
        signature = ECDSA.calculateECDSASignature(privateKey, data);
    }

    public boolean verifySignature() {
        String data = Encoder.encode(senderPublicKeyAddress) + Encoder.encode(reciepientPublicKeyAddress) + value;
        return ECDSA.verifyECDSASignature(senderPublicKeyAddress, data, signature);
    }

}
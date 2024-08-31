package crypto.blockchain.signed;

import crypto.blockchain.BlockDataHashable;
import crypto.blockchain.Request;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;


public class SignedDataRequest implements BlockDataHashable, Request {

    String publicKeyAddress;
    String value;
    String signedDataHashHex;
    String signature;

    public SignedDataRequest(String publicKeyAddress, String value) {
        this.publicKeyAddress = publicKeyAddress;
        this.value = value;
        this.signedDataHashHex = Encoder.encodeToHexadecimal(calculateSignedDataHash());
    }

    private byte[] calculateSignedDataHash() {
        String preHash = publicKeyAddress + value;
        return Hashing.hash(preHash);
    }

    public String generateValueHash() {
        String preHash = value + getSignedDataRequestHashHex();
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String getValue(){
        return value;
    }

    public String getSignedDataRequestHashHex() {
        return signedDataHashHex;
    }

    @Override
    public String getBlockDataHash() {
        return getSignedDataRequestHashHex();
    }

    public String getSignature() {
        return signature;
    }

    public String getPublicKeyAddress(){
        return publicKeyAddress;
    }

    public void setSignature(byte[] signature) {
        this.signature =  Encoder.encodeToHexadecimal(signature);
    }
}

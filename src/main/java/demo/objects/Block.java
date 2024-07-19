package demo.objects;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Block {
    public Object dataObject;
    public String dataString;
    public String previousBlockHashId;
    public int nonce;
    public String blockHashId;

    public Block(Object dataObject, String dataAsString, String previousHashId) {
        this.dataObject = dataObject;
        this.dataString = dataAsString;
        this.previousBlockHashId = previousHashId;
    }

    public boolean validateHash() throws Exception {
        String hash = calculateHash(this.nonce);
        return hash.equals(this.blockHashId);
    }

    public String calculateHash(int nonce) throws Exception {
        byte[] prehash = (this.previousBlockHashId + nonce + dataString).getBytes(UTF_8);
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(prehash);
        StringBuilder hashStringRepresentation = new StringBuilder();
        for (byte b : hash) {
            hashStringRepresentation.append(String.format("%02x", b));
        }
        return hashStringRepresentation.toString();
    }

    public void setNonce(int nonce){
        this.nonce = nonce;
    }

}

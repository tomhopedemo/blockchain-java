package demo.objects;

import demo.blockchain.BlockDataHashable;
import demo.blockchain.TransactionRequest;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Block {
    public Object blockData; //i don't want to do this, it's only for gson
    //is there some way around this - e.g. a transaction request block.
    public String blockDataHash;
    public String previousBlockHashId;

    //these are set later
    public int nonce;
    public String blockHashId;

    public Block(BlockDataHashable blockDataHashable, String previousHashId) throws Exception {
        this.blockData =  blockDataHashable;
        this.blockDataHash = blockDataHashable.blockDataHash();
        this.previousBlockHashId = previousHashId;
    }

    public String getPreHash(int nonce){
        return previousBlockHashId + nonce + blockDataHash;
    }

    public String getPreHash(){
        return getPreHash(nonce);
    }

    public boolean validateHash() throws Exception {
        String hash = calculateHash(this.nonce);
        return hash.equals(this.blockHashId);
    }

    public String calculateHash(int nonce) throws Exception {
        byte[] prehash = (this.previousBlockHashId + nonce + blockDataHash).getBytes(UTF_8);
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

    public void setBlockHashId(String blockHashId){
        this.blockHashId = blockHashId;
    }

    public String getDataString(){
        return blockDataHash;
    }

    public String getPreviousBlockHashId(){
        return previousBlockHashId;
    }

    public String getBlockHashId() {
        return blockHashId;
    }
}

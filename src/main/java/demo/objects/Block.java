package demo.objects;

import demo.blockchain.BlockHashable;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Block {
    public Object dataObject;
    public String dataString;
    public String previousBlockHashId;

    //these are set later
    public int nonce;
    public String blockHashId;


    //the block needs to be deterministically converted into a datastring for the hash
    //but it doesn't need to be converted back. //so we can have a function
    // to create the blockhash from the dataObject

    public Block(BlockHashable blockhashable, String previousHashId) throws Exception {
        this.dataObject = blockhashable;
        this.dataString = blockhashable.blockhash();
        this.previousBlockHashId = previousHashId;
    }

    public String getPreHash(int nonce){
        return previousBlockHashId + nonce + dataString;
    }

    public String getPreHash(){
        return getPreHash(nonce);
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

    public void setBlockHashId(String blockHashId){
        this.blockHashId = blockHashId;
    }

    public String getDataString(){
        return dataString;
    }

    public String getPreviousBlockHashId(){
        return previousBlockHashId;
    }

    public String getBlockHashId() {
        return blockHashId;
    }
}

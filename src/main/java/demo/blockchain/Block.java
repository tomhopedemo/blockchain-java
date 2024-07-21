package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

public class Block {
    public Object blockData;
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

    //CSVtoJson etc. can be moved to a different project.
    //create a new project - playground2 for this
    //do we use this/need to use this in the blockchainvalidation
    public boolean validateHash() throws Exception {
        String hash = calculateHash(this.nonce);
        return hash.equals(this.blockHashId);
    }

    public String calculateHash(int nonce) throws Exception {
        byte[] hash = Hashing.hash(this.previousBlockHashId + nonce + blockDataHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public void setNonce(int nonce){
        this.nonce = nonce;
    }

    public void setBlockHashId(String blockHashId){
        this.blockHashId = blockHashId;
    }

    public String getPreviousBlockHashId(){
        return previousBlockHashId;
    }

    public String getBlockHashId() {
        return blockHashId;
    }
}

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

    public String calculateHash(int nonce) throws Exception {
        String preHash = previousBlockHashId + nonce + blockDataHash;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String calculateHash() throws Exception {
        return calculateHash(this.nonce);
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

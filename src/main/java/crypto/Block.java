package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public class Block {
    public Object blockData;
    public String blockDataHash;
    public String previousBlockHashId;

    //these are set later
    public int nonce;
    public String blockHashId;

    public Block(BlockDataHashable blockDataHashable, String previousHashId) {
        this.blockData =  blockDataHashable;
        this.blockDataHash = blockDataHashable.getBlockDataHash();
        this.previousBlockHashId = previousHashId == null ? "" : previousHashId;
    }

    //modify to an index based approach
    public String calculateHash(int nonce) {
        String preHash = previousBlockHashId + nonce + blockDataHash;
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String calculateHash() {
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

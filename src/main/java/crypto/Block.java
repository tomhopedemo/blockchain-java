package crypto;

import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

public class Block {
    public Object blockData;
    public String blockDataHash;
    public Hashing.Type hashType;
    public String previousBlockHashId;


    //these are set later
    public int nonce;
    public String blockHashId;

    public Block(BlockDataHashable blockDataHashable, String previousHashId, Hashing.Type hashType) {
        this.blockData =  blockDataHashable;
        this.blockDataHash = blockDataHashable.getBlockDataHash(hashType);
        this.hashType = hashType;
        this.previousBlockHashId = previousHashId == null ? "" : previousHashId;
    }

    //modify to an index based approach
    public String calculateHash(int nonce, Hashing.Type hashType) {
        String preHash = previousBlockHashId + nonce + blockDataHash;
        byte[] hash = Hashing.hash(preHash, hashType);
        return Encoder.encodeToHexadecimal(hash);
    }

    public String calculateHash(Hashing.Type hashType) {
        return calculateHash(this.nonce, hashType);
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

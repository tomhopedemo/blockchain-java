package demo.objects;

public class Block {
    public String hash; //id of the block
    public String previousHash; //id of previous block
    public String data; //majority of information encoded in the block

    public int nonce; //essential

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
    }

}

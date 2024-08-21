package crypto.blockchain;

public class BlockMiner {

    public Block block;

    public BlockMiner(Block block) {
        this.block = block;
    }

    public void mineBlockHash(String blockHashPrefixMatch) {
        int nonce = 0;
        while (true){
            String blockHash = block.calculateHash(++nonce);
            if (blockHash.startsWith(blockHashPrefixMatch)){
                block.setBlockHashId(blockHash);
                block.setNonce(nonce);
                return;
            }
        }
    }
}

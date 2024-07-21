package demo.blockchain;

import demo.encoding.Encoder;
import demo.hashing.Hashing;

public class BlockMiner {

    public Block block;

    public BlockMiner(Block block) {
        this.block = block;
    }

    public void mineBlockHash(String blockHashPrefixMatch) throws Exception {
        int nonce = 0;
        while (true){
            byte[] blockHash = Hashing.hash(block.getPreHash(++nonce));
            String blockHashHexEncoding = Encoder.encodeToHexadecimal(blockHash);
            if (blockHashHexEncoding.startsWith(blockHashPrefixMatch)){
                block.setBlockHashId(blockHashHexEncoding);
                block.setNonce(nonce);
                return;
            }
        }
    }
}

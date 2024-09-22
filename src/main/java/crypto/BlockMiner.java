package crypto;

import crypto.hashing.Hashing;

public class BlockMiner {

    public static void mineBlockHash(Block block, String blockHashPrefixMatch, Hashing.Type hashType) {
        int nonce = 0; //yeah - not the way we're going to do
        while (true){
            String blockHash = block.calculateHash(++nonce, hashType);
            if (blockHash.startsWith(blockHashPrefixMatch)){
                block.setBlockHashId(blockHash);
                block.setNonce(nonce);
                return;
            }
        }
    }
}

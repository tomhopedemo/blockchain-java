package crypto.blockchain;

public class BlockMiner {

    public static void mineBlockHash(Block block, String blockHashPrefixMatch) {
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

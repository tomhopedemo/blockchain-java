package demo.blockchain;

import demo.objects.Block;

public class BlockMiner {

    public Block block;

    public BlockMiner(Block block) {
        this.block = block;
    }

    public void mineHash(int difficultyPrefixLength) {
        int nonce = 0;
        String hash = null;
        BlockHashCalculator hashCalculator = new BlockHashCalculator(block);
        String prefixString = new String(new char[difficultyPrefixLength]).replace('\0', '0');
        while (hash == null || !hash.substring(0, difficultyPrefixLength).equals(prefixString)) {
            nonce++;
            hash = hashCalculator.calculate(nonce);
        }
        block.hash = hash;
        block.nonce = nonce;
    }
}

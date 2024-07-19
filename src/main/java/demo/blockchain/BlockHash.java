package demo.blockchain;

import demo.objects.Block;

import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BlockHash {

    public Block block;

    public BlockHash(Block block) {
        this.block = block;
    }

    public boolean validate() throws Exception {
        String hash = calculate(block.nonce);
        return hash.equals(block.blockHashId);
    }

    public String calculate(int nonce) throws Exception {
        byte[] prehash = (block.previousBlockHashId + nonce + block.dataString).getBytes(UTF_8);
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(prehash);
        StringBuilder hashStringRepresentation = new StringBuilder();
        for (byte b : hash) {
            hashStringRepresentation.append(String.format("%02x", b));
        }
        return hashStringRepresentation.toString();
    }

}

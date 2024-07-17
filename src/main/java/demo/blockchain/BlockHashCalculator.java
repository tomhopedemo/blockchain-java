package demo.blockchain;

import demo.objects.Block;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BlockHashCalculator {

    public Block block;

    public BlockHashCalculator(Block block) {
        this.block = block;
    }

    public boolean validate(){
        String hash = calculate(block.nonce);
        return hash.equals(block.hash);
    }

    public String calculate(int nonce) {
        String dataToHash = block.previousHash + nonce + block.data;
        byte[] bytes = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException _) {
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

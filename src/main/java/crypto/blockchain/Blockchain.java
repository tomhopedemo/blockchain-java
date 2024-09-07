package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    public String id;
    public List<Block> blocks = new ArrayList<>();

    public Blockchain(String id) {
        this.id = id;
    }

    public void add(Block block){
        this.blocks.add(block);
    }

    public String getMostRecentHash(){
        if (blocks.isEmpty()) return null;
        return blocks.getLast().getBlockHashId();
    }

    public Block get(int index){
        return blocks.get(index);
    }

}

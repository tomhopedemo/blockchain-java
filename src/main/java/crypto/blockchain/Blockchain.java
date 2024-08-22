package crypto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    public String id;
    public List<Block> blocks = new ArrayList<>();

    public boolean valid = true;

    public Blockchain(String id) {
        this.id = id;
    }

    public void add(Block block){
        this.blocks.add(block);
    }

    public Block getMostRecent(){
        if (blocks.isEmpty()){
            return null;
        }
        return blocks.getLast();
    }

    public Block get(int index){
        return blocks.get(index);
    }

    public String getId(){
        return id;
    }

}

package demo.blockchain;

import demo.objects.Block;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    public String id;
    public List<Block> blocks = new ArrayList<>();
    public boolean valid = true;

    public Blockchain(Block block, String id) {
        blocks.add(block);
        this.id = id;
    }

    public void add(Block block){
        this.blocks.add(block);
    }

    public Block getMostRecent(){
        return blocks.getLast();
    }

}

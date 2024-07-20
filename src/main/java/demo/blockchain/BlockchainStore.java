package demo.blockchain;

import java.util.ArrayList;
import java.util.List;

public class BlockchainStore {
    public List<Blockchain> blockchains = new ArrayList<>();

    public BlockchainStore() {
    }

    public void add(Blockchain blockchain){
        blockchains.add(blockchain);
    }

    public List<Blockchain> getBlockchains(){
        return blockchains;
    }
}

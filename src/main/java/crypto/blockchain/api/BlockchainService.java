package crypto.blockchain.api;

import crypto.blockchain.Blockchain;
import crypto.blockchain.BlockchainException;
import crypto.blockchain.account.MultiAccountBasedBlockchainTechnology;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockchainService {

    static Map<String, Blockchain> blockchains = new ConcurrentHashMap<>();

    public static Blockchain getBlockchain(String id){
        return blockchains.get(id);
    }

    public static Blockchain createBlockchain(String id){
        try {
            new MultiAccountBasedBlockchainTechnology().execute(id, 1, 100);
        } catch (BlockchainException e){
        }
        return getBlockchain(id);
    }

    public static void addBlockchain(String id, Blockchain blockchain){
        blockchains.put(id, blockchain);
    }
}

package crypto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinerPool implements Runnable {

    private final Map<String, Miner> miners = new ConcurrentHashMap<>();

    private final LinkedHashSet<String> minerRequests = new LinkedHashSet<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);;

    private final Set<String> disabledChains = new LinkedHashSet<>();

    private static final MinerPool MINER_POOL = new MinerPool();

    @Override
    public void run() {
        while (true){
            Set<String> running = new HashSet<>(miners.keySet());
            ArrayList<String> existingRequests = new ArrayList<>(minerRequests);
            if (!existingRequests.isEmpty()){
                for (String minerRequest : existingRequests) {
                    if (!running.contains(minerRequest)){
                        Miner miner = new Miner(minerRequest);
                        minerRequests.remove(minerRequest);
                        miners.put(minerRequest, miner);
                        executorService.submit(miner);
                        break;
                    }
                }
            } try {
                Thread.sleep(1000);
            } catch (InterruptedException interrupt) {
                throw new UnsupportedOperationException(interrupt);
            }
        }
    }

    public static MinerPool getInstance(){
        return MINER_POOL;
    }

    public static void requestMiner(String id){
        MinerPool instance = getInstance();
        if (!instance.disabledChains.contains(id)){
            instance.minerRequests.add(id);
        }
    }

    public static void disable(String id) {
        getInstance().disabledChains.add(id);
    }

    public static void start(){
        new Thread(getInstance()).start();
    }

    public static void removeMiner(String id) {
        getInstance().miners.remove(id);
    }

}

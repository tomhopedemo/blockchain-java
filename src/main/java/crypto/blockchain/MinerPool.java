package crypto.blockchain;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinerPool {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);;

    public static void addMiner(Miner miner){
        executorService.submit(miner);
    }


}

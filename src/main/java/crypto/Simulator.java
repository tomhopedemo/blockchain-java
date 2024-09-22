package crypto;

import crypto.block.*;
import crypto.service.AuxService;
import crypto.service.ChainService;

import java.util.Random;

public class Simulator {
    private final String id;
    private final ChainService chainService;
    private final AuxService auxService;

    public static final String CURRENCY = "CBX";

    public Simulator(String id) {
        this.id = id;
        chainService = new ChainService(id);
        auxService = new AuxService(id);
    }

    public Keypair account(Keypair keypair) throws ChainException {
        chainService.allowBlockType(Transaction.class);
        auxService.registerKeypair(keypair);
        Request genesisRequest = auxService.account(keypair.publicKey(), keypair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        Keypair to = auxService.keypair();
        Request request = auxService.account(keypair.publicKey(), to.publicKey(), CURRENCY, 5L);
        chainService.submitRequest(request);
        miner.runSynch();
        return to;
    }


    public void stake(Keypair keypair) throws ChainException {
        chainService.allowBlockType(Stake.class);
        Request request = auxService.stake(keypair, CURRENCY);
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public String currency(Keypair keypair) throws ChainException {
        chainService.allowBlockType(Currency.class);
        Currency request = Currency.create(keypair, CURRENCY);
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
        return CURRENCY;
    }


    public void difficulty(String currency, Keypair keypair) {
        chainService.allowBlockType(Difficulty.class);
        Difficulty request = new Difficulty(1, currency, keypair.publicKey());
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public Keypair keypair() {
        chainService.allowBlockType(Keypair.class);
        Keypair keypair = auxService.keypair();
        chainService.submitRequest(keypair);
        Miner miner = new Miner(id);
        miner.runSynch();
        return keypair;
    }

    public void signed() throws ChainException {
        chainService.allowBlockType(Signed.class);
        Keypair keypair = auxService.keypair();
        auxService.registerKeypair(keypair);
        Request request = auxService.signed(keypair.publicKey(), "ABCDE");
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void data()  {
        chainService.allowBlockType(Data.class);
        Request request = new Data(randomString(10), "myformat");
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void utxo() throws ChainException {
        chainService.allowBlockType(UTXO.class);
        Keypair keypair = auxService.keypair();
        auxService.registerKeypair(keypair);

        Request genesisRequest = auxService.utxoGenesis(keypair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        Keypair toKeypair = auxService.keypair();
        Request request = auxService.utxo(keypair.publicKey(), toKeypair.publicKey(), CURRENCY, 5L);
        chainService.submitRequest(request);
        miner.runSynch();
    }

    private String randomString(int length){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

}

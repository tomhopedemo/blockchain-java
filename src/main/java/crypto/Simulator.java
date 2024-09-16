package crypto;

import crypto.block.Currency;
import crypto.block.Data;
import crypto.block.Difficulty;
import crypto.block.Keypair;
import crypto.service.AuxService;
import crypto.service.ChainService;

import java.util.Random;

import static crypto.BlockType.*;
import static crypto.BlockType.UTXO;

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
        chainService.allowBlockType(ACCOUNT);
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
        chainService.allowBlockType(STAKE);
        Request request = auxService.stake(keypair, CURRENCY);
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public String currency(Keypair keypair) {
        chainService.allowBlockType(BlockType.CURRENCY);
        Currency request = new Currency(CURRENCY, keypair.publicKey());
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
        return CURRENCY;
    }


    public void difficulty(String currency, Keypair keypair) {
        chainService.allowBlockType(DIFFICULTY);
        Difficulty request = new Difficulty(1, currency, keypair.publicKey());
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public Keypair keypair() {
        chainService.allowBlockType(KEYPAIR);
        Keypair keypair = auxService.keypair();
        chainService.submitRequest(keypair);
        Miner miner = new Miner(id);
        miner.runSynch();
        return keypair;
    }

    public void signed() throws ChainException {
        chainService.allowBlockType(SIGNED);
        Keypair keypair = auxService.keypair();
        auxService.registerKeypair(keypair);
        Request request = auxService.signed(keypair.publicKey(), "ABCDE");
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void data()  {
        chainService.allowBlockType(DATA);
        Request request = new Data(randomString(10));
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void utxo() throws ChainException {
        chainService.allowBlockType(UTXO);
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

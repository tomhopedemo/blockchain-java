package crypto.blockchain;

import crypto.block.currency.CurrencyRequest;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;

import java.util.Random;

import static crypto.blockchain.BlockType.*;
import static crypto.blockchain.BlockType.UTXO;

public class Simulator {
    public Simulator(String id) {this.id = id;}

    private String id;
    private final ChainService chainService = new ChainService(id) ;
    private final AuxService auxService = new AuxService(id);
    public static final String CURRENCY = "CBX";

    public void account() throws ChainException {
        chainService.allowBlockType(ACCOUNT);
        Keypair genesisKeypair = auxService.keypair();
        auxService.registerKeypair(genesisKeypair);
        Request genesisRequest = auxService.genesisRequest(ACCOUNT, genesisKeypair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        Keypair to = auxService.keypair();
        Request request = auxService.account(genesisKeypair.publicKey(), to.publicKey(), CURRENCY, 5L);
        chainService.submitRequest(request);
        miner.runSynch();
    }

    public void currency() {
        chainService.allowBlockType(BlockType.CURRENCY);
        String key = auxService.key();
        CurrencyRequest request = new CurrencyRequest(CURRENCY, key);
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void keypair() {
        chainService.allowBlockType(KEYPAIR);
        Keypair keypair = auxService.keypair();
        chainService.submitRequest(keypair);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void signed() throws ChainException {
        chainService.allowBlockType(SIGNED_DATA);
        Keypair keypair = auxService.keypair();
        auxService.registerKeypair(keypair);
        Request request = auxService.signed(keypair.publicKey(), "ABCDE");
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void simple()  {
        chainService.allowBlockType(DATA);
        Request request = new DataRequest(randomString(10));
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void utxo() throws ChainException {
        chainService.allowBlockType(UTXO);
        Keypair keypair = auxService.keypair();
        auxService.registerKeypair(keypair);

        Request genesisRequest = auxService.genesisRequest(UTXO, keypair.publicKey(), CURRENCY, 100L);
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

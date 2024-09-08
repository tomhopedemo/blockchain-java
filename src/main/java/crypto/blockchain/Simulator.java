package crypto.blockchain;

import crypto.block.currency.CurrencyRequest;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;

import java.util.Random;

import static crypto.blockchain.BlockType.*;
import static crypto.blockchain.BlockType.UTXO;

public class Simulator {
    public Simulator(String id) {this.id = id;}

    private String id;
    private final ChainService chainService = new ChainService(id) ;
    private final AuxService auxService = new AuxService();
    public static final String CURRENCY = "CBX";

    public void account() throws ChainException {
        chainService.allowBlockType(ACCOUNT);
        KeyPair genesisKeypair = auxService.keypair();
        auxService.registerKeyPair(genesisKeypair.publicKey(), genesisKeypair.privateKey());
        Request genesisRequest = auxService.genesisRequest(id, ACCOUNT, genesisKeypair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair to = auxService.keypair();
        TransactionRequestParams params = new TransactionRequestParams.Builder().setCurrency(CURRENCY).setFrom(genesisKeypair.publicKey()).setTo(to.publicKey()).setValue(5L).build();
        Request request = auxService.account(id, params);
        chainService.submitRequest(request);
        miner.runSynch();
    }

    public void currency() {
        chainService.allowBlockType(BlockType.CURRENCY);
        String key = auxService.key(id);
        CurrencyRequest request = new CurrencyRequest(CURRENCY, key);
        chainService.submitRequest(request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void keypair() {
        chainService.allowBlockType(KEYPAIR);
        KeyPair keyPair = auxService.keypair();
        chainService.submitRequest(keyPair);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void signed() throws ChainException {
        chainService.allowBlockType(SIGNED_DATA);
        KeyPair keyPair = auxService.keypair();
        auxService.registerKeyPair(keyPair.publicKey(), keyPair.privateKey());
        Request request = auxService.signed(id, keyPair.publicKey(), "ABCDE");
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
        KeyPair keyPair = auxService.keypair();
        auxService.registerKeyPair(keyPair.publicKey(), keyPair.privateKey());

        Request genesisRequest = auxService.genesisRequest(id, UTXO, keyPair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair toKeyPair = auxService.keypair();
        TransactionRequestParams params = new TransactionRequestParams.Builder().setCurrency(CURRENCY).setFrom(keyPair.publicKey()).setTo(toKeyPair.publicKey()).setValue(5L).build();
        Request request = auxService.utxo(id, params);
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

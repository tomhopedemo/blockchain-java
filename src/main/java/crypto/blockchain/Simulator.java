package crypto.blockchain;

import crypto.block.currency.CurrencyRequest;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.service.AuxService;
import crypto.blockchain.service.ChainService;

import java.util.Random;

import static crypto.blockchain.BlockType.*;
import static crypto.blockchain.BlockType.UTXO;

public record Simulator(String id) {

    private static final ChainService chainService = new ChainService();
    private static final AuxService auxService = new AuxService();
    public static final String CURRENCY = "CBX";

    public void account() throws ChainException {
        chainService.allowBlockType(id, ACCOUNT);
        KeyPair genesisKeypair = auxService.keypair();
        auxService.registerKeyPair(genesisKeypair.publicKey(), genesisKeypair.privateKey());
        Request genesisRequest = auxService.genesisRequest(id, ACCOUNT, genesisKeypair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(id, genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair to = auxService.keypair();
        TransactionRequestParams params = new TransactionRequestParams.Builder().setCurrency(CURRENCY).setFrom(genesisKeypair.publicKey()).setTo(to.publicKey()).setValue(5L).build();
        Request request = auxService.account(id, params);
        chainService.submitRequest(id, request);
        miner.runSynch();
    }

    public void currency() {
        chainService.allowBlockType(id, BlockType.CURRENCY);
        String key = auxService.key(id);
        CurrencyRequest request = new CurrencyRequest(CURRENCY, key);
        chainService.submitRequest(id, request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void keypair() {
        chainService.allowBlockType(id, KEYPAIR);
        KeyPair keyPair = auxService.keypair();
        chainService.submitRequest(id, keyPair);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void signed() throws ChainException {
        chainService.allowBlockType(id, SIGNED_DATA);
        KeyPair keyPair = auxService.keypair();
        auxService.registerKeyPair(keyPair.publicKey(), keyPair.privateKey());
        Request request = auxService.signed(id, keyPair.publicKey(), "ABCDE");
        chainService.submitRequest(id, request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void simple()  {
        chainService.allowBlockType(id, DATA);
        Request request = new DataRequest(randomString(10));
        chainService.submitRequest(id, request);
        Miner miner = new Miner(id);
        miner.runSynch();
    }

    public void utxo() throws ChainException {
        chainService.allowBlockType(id, UTXO);
        KeyPair keyPair = auxService.keypair();
        auxService.registerKeyPair(keyPair.publicKey(), keyPair.privateKey());

        Request genesisRequest = auxService.genesisRequest(id, UTXO, keyPair.publicKey(), CURRENCY, 100L);
        chainService.submitRequest(id, genesisRequest);
        Miner miner = new Miner(id);
        miner.runSynch();

        KeyPair toKeyPair = auxService.keypair();
        TransactionRequestParams params = new TransactionRequestParams.Builder().setCurrency(CURRENCY).setFrom(keyPair.publicKey()).setTo(toKeyPair.publicKey()).setValue(5L).build();
        Request request = auxService.utxo(id, params);
        chainService.submitRequest(id, request);
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

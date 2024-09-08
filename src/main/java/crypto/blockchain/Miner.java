package crypto.blockchain;

import crypto.block.account.AccountFactory;
import crypto.block.currency.CurrencyFactory;
import crypto.block.keypair.KeyPairFactory;
import crypto.block.signed.SignedFactory;
import crypto.block.simple.SimpleBlockFactory;
import crypto.block.utxo.UTXOFactory;

import java.util.List;
import java.util.Set;

public record Miner (String id) implements Runnable {

    @Override
    public void run() {
        try {
            runSynch();
        } finally {
            MinerPool.removeMiner(id);
        }
    }

    public void runSynch() {
        Set<BlockType> blockTypes = Data.getBlockTypes(id);
        for (BlockType blockType : blockTypes) {
            List<? extends Request> requests = Requests.get(id, blockType);
            if (requests == null || requests.isEmpty()) {
                continue;
            }

            BlockFactory blockFactory = switch (blockType) {
                case DATA -> new SimpleBlockFactory(id);
                case SIGNED_DATA -> new SignedFactory(id);
                case CURRENCY -> new CurrencyFactory(id);
                case KEYPAIR -> new KeyPairFactory(id);
                case ACCOUNT -> new AccountFactory(id);
                case UTXO -> new UTXOFactory(id);
            };

            BlockData blockDataHashable = blockFactory.prepare(requests);
            if (blockDataHashable != null) {
                blockFactory.mine(blockDataHashable);
            }
        }
    }
}

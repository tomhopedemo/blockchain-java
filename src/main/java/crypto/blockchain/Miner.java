package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionsBlockFactory;
import crypto.blockchain.signed.SignedBlockFactory;
import crypto.blockchain.simple.SimpleBlockFactory;
import crypto.blockchain.utxo.UTXOBlockFactory;

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
                case SIGNED_DATA -> new SignedBlockFactory(id);
                case CURRENCY -> new CurrencyBlockFactory(id);
                case ACCOUNT -> new AccountTransactionsBlockFactory(id);
                case UTXO -> new UTXOBlockFactory(id);
            };

            BlockDataHashable blockDataHashable = blockFactory.prepareRequests(requests);
            if (blockDataHashable != null) {
                blockFactory.mineNextBlock(blockDataHashable);
            }
        }
    }
}

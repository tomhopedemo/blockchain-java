package crypto.blockchain;

import crypto.blockchain.account.AccountChain;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequests;
import crypto.blockchain.simple.SimpleChain;
import crypto.blockchain.utxo.UTXOChain;
import crypto.blockchain.utxo.UTXORequest;
import crypto.blockchain.utxo.UTXORequests;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record Miner (String id) implements Runnable {

    @Override
    public void run() {
        try {
            Set<BlockType> blockTypes = Data.getBlockTypes(id);
            for (BlockType blockType : blockTypes) {
                List<? extends Request> requests = Requests.get(id, blockType);
                if (requests == null || requests.isEmpty()) {
                    continue;
                }
                switch (blockType) {
                    case DATA -> {
                        SimpleChain dataChain = new SimpleChain(id);
                        Optional<List<DataRequest>> dataRequests = dataChain.prepareRequests((List<DataRequest>) requests);
                        if (dataRequests.isPresent()) {
                            dataChain.mineNextBlock(dataRequests.get());
                        }
                    }
                    case ACCOUNT -> {
                        AccountChain accountChain = new AccountChain(id);
                        Optional<AccountTransactionRequests> accountTransactionRequests = accountChain.prepareRequests((List<AccountTransactionRequest>) requests);
                        if (accountTransactionRequests.isPresent()) {
                            accountChain.mineNextBlock(accountTransactionRequests.get());
                        }
                    }
                    case UTXO -> {
                        UTXOChain utxoChain = new UTXOChain(id);
                        Optional<UTXORequests> utxoRequests = utxoChain.prepareRequests((List<UTXORequest>) requests);
                        if (utxoRequests.isPresent()) {
                            utxoChain.mineNextBlock(utxoRequests.get(), 1);
                        }
                    }
                }
            }
        } finally {
            MinerPool.removeMiner(id);
        }
    }
}

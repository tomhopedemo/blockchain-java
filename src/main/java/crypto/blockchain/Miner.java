package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionsBlockFactory;
import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequests;
import crypto.blockchain.signed.BlockDataWrapper;
import crypto.blockchain.signed.SignedChain;
import crypto.blockchain.signed.SignedDataRequest;
import crypto.blockchain.simple.SimpleChain;
import crypto.blockchain.utxo.UTXOBlockFactory;
import crypto.blockchain.utxo.UTXORequest;
import crypto.blockchain.utxo.UTXORequests;

import java.util.List;
import java.util.Optional;
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
            switch (blockType) {
                case DATA -> {
                    SimpleChain dataChain = new SimpleChain(id);
                    Optional<List<DataRequest>> dataRequests = dataChain.prepareRequests((List<DataRequest>) requests);
                    if (dataRequests.isPresent()) {
                        dataChain.mineNextBlock(dataRequests.get());
                    }
                }
                case SIGNED_DATA -> {
                    SignedChain signedDataChain = new SignedChain(id);
                    Optional<BlockDataWrapper> dataRequests = signedDataChain.prepareRequests((List<SignedDataRequest>) requests);
                    if (dataRequests.isPresent()) {
                        signedDataChain.mineNextBlock(dataRequests.get());
                    }
                }
                case ACCOUNT -> {
                    AccountTransactionsBlockFactory accountTransactionsBlockFactory = new AccountTransactionsBlockFactory(id);
                    Optional<AccountTransactionRequests> accountTransactionRequests = accountTransactionsBlockFactory.prepareRequests((List<AccountTransactionRequest>) requests);
                    if (accountTransactionRequests.isPresent()) {
                        accountTransactionsBlockFactory.mineNextBlock(accountTransactionRequests.get());
                    }
                }
                case UTXO -> {
                    UTXOBlockFactory utxoBlockFactory = new UTXOBlockFactory(id);
                    Optional<UTXORequests> utxoRequests = utxoBlockFactory.prepareRequests((List<UTXORequest>) requests);
                    if (utxoRequests.isPresent()) {
                        utxoBlockFactory.mineNextBlock(utxoRequests.get());
                    }
                }
            }
        }
    }
}

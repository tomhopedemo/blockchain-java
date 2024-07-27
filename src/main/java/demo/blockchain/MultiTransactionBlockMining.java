package demo.blockchain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiTransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public TransactionCache transactionCache;
    public TransactionVerification transactionVerification;

    public MultiTransactionBlockMining(Blockchain blockchain, int difficulty, TransactionCache transactionCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.transactionCache = transactionCache;
        this.transactionVerification = new TransactionVerification(transactionCache);
    }

    public TransactionRequests constructTransactionRequestsForNextBlock(List<TransactionRequest> availableTransactionRequests) throws Exception {
        Set<String> inputsToInclude = new HashSet<>();
        List<TransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (TransactionRequest transactionRequest : availableTransactionRequests) {
            //verify signature
            boolean verified = transactionVerification.verify(transactionRequest, false);
            if (!verified){
                continue;
            }

            //consider for inclusion
            List<String> transactionInputsToAdd = new ArrayList<>();
            boolean include = true;
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                if (transactionInputsToAdd.contains(transactionInput.getTransactionOutputHash()) || inputsToInclude.contains(transactionInput.getTransactionOutputHash())){
                    include = false;
                    break;
                } else {
                    transactionInputsToAdd.add(transactionInput.getTransactionOutputHash());
                }
            }
            if (include) {
                transactionRequestsToInclude.add(transactionRequest);
                inputsToInclude.addAll(transactionRequest.getTransactionInputs().stream().map(t -> t.getTransactionOutputHash()).toList());
            }
        }
        return new TransactionRequests(transactionRequestsToInclude);
    }

    public void mineNextBlock(TransactionRequests transactionRequests) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null; //indicative of genesis block - will tidy

        //Individual Transaction Verification
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            boolean verified = transactionVerification.verify(transactionRequest, skipEqualityCheck);
            if (!verified){
                return;
            }
        }

        //Overall Verification
        Set<String> inputs = new HashSet<>();
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                if (inputs.contains(transactionInput.getTransactionOutputHash())){
                    return;
                } else {
                    inputs.add(transactionInput.getTransactionOutputHash());
                }
            }
        }

        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                transactionCache.remove(transactionInput.getTransactionOutputHash());
            }
        }
    }

    private boolean checkInputSumEqualToOutputSum(TransactionRequest transactionRequest) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = transactionCache.get(transactionInput.getTransactionOutputHash());
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfInputs += transactionOutputValue;
        }

        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfOutputs += transactionOutputValue;
        }

        return sumOfInputs == sumOfOutputs;
    }

}

package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

import static crypto.blockchain.BlockType.UTXO;

public record UTXOBlockFactory(String id) implements BlockFactory<UTXORequests, UTXORequest>{

    @Override
    public void mineNextBlock(UTXORequests requests) {
        Blockchain chain = Data.getChain(id);
        Block mostRecentBlock = chain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();

        //Individual Transaction Verification
        if (chain.getMostRecent() != null) {
            for (UTXORequest transactionRequest : requests.getTransactionRequests()) {
                boolean verified = UTXOVerification.verifySignature(transactionRequest, id);
                if (!verified) {
                    return;
                }
                boolean inputSumEqualsOutputSum = checkInputSumEqualToOutputSum(transactionRequest, id);
                if (!inputSumEqualsOutputSum) {
                    return;
                }
            }
        }

        //Overall Verification (no repeats)
        Set<String> inputs = new HashSet<>();
        for (UTXORequest utxoRequest : requests.getTransactionRequests()) {
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                if (inputs.contains(transactionInput.transactionOutputHash())){
                    return;
                } else {
                    inputs.add(transactionInput.transactionOutputHash());
                }
            }
        }

        //Create block
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        chain.add(block);

        //Update Caches
        for (UTXORequest utxoRequest : requests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : utxoRequest.getTransactionOutputs()) {
                Data.addUtxo(id, transactionOutput.generateTransactionOutputHash(utxoRequest.getTransactionRequestHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                Data.removeUtxo(id, transactionInput.transactionOutputHash());
            }
        }
        Requests.remove(id, requests.getTransactionRequests(), UTXO);
    }

    @Override
    public UTXORequests prepareRequests(List<UTXORequest> availableRequests) {
        Set<String> inputsReferenced = new HashSet<>();
        List<UTXORequest> requests = new ArrayList<>();
        Blockchain chain = Data.getChain(id);
        if (chain.getMostRecent() == null){
            availableRequests = availableRequests.stream().filter(r -> r.transactionInputs.isEmpty()).toList();
        }

        for (UTXORequest request : availableRequests) {
            boolean shouldInclude;
            if (chain.getMostRecent() == null){
                shouldInclude = includeGenesisRequest(request);
            } else {
                shouldInclude = includeUtxoRequest(request, inputsReferenced);
            }

            if (shouldInclude) {
                requests.add(request);
                inputsReferenced.addAll(request.getTransactionInputs().stream().map(t -> t.transactionOutputHash()).toList());
            }
        }
        return requests.isEmpty() ? null : new UTXORequests(requests);
    }

    private boolean includeGenesisRequest(UTXORequest request) {
        return true; //can add restrictions later
    }

    private boolean includeUtxoRequest(UTXORequest request, Set<String> inputsReferenced) {
        if (!UTXOVerification.verifySignature(request, id)) {
            return false;
        }

        List<String> transactionInputsToAdd = new ArrayList<>();
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            //check if input has already been used in this same transaction request
            String transactionOutputHash = transactionInput.transactionOutputHash();
            if (transactionInputsToAdd.contains(transactionOutputHash)){
                return false;
                //check if input is already added to the set of transactions for this block;
            } else if (inputsReferenced.contains(transactionOutputHash)){
                return false;
                //check if input is available
            } else if (!Data.hasUtxo(id, transactionOutputHash)) {
                return false;
            } else {
                transactionInputsToAdd.add(transactionOutputHash);
            }
        }

        return true;
    }

    private static boolean checkInputSumEqualToOutputSum(UTXORequest transactionRequest, String id) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = Data.getUtxo(id, transactionInput.transactionOutputHash());
            long transactionOutputValue = transactionOutput.getValue();
            sumOfInputs += transactionOutputValue;
        }

        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            long transactionOutputValue = transactionOutput.getValue();
            sumOfOutputs += transactionOutputValue;
        }

        return sumOfInputs == sumOfOutputs;
    }

}

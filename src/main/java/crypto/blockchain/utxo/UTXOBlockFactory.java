package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

import static crypto.blockchain.BlockType.UTXO;

public record UTXOBlockFactory(String id) implements BlockFactory<UTXORequests, UTXORequest>{

    @Override
    public void mineNextBlock(UTXORequests requests) {
        Blockchain blockchain = Data.getChain(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null; //indicative of genesis block - will tidy

        //Individual Transaction Verification
        for (UTXORequest transactionRequest : requests.getTransactionRequests()) {
            boolean verified = UTXOVerification.verifySignature(transactionRequest, skipEqualityCheck, id);
            if (!verified){
                return;
            }
            boolean inputSumEqualsOutputSum = checkInputSumEqualToOutputSum(transactionRequest, id);
            if (!inputSumEqualsOutputSum){
                return;
            }
        }

        //Overall Verification (no repeats)
        Set<String> inputs = new HashSet<>();
        for (UTXORequest utxoRequest : requests.getTransactionRequests()) {
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                if (inputs.contains(transactionInput.getTransactionOutputHash())){
                    return;
                } else {
                    inputs.add(transactionInput.getTransactionOutputHash());
                }
            }
        }

        //Create block
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);

        //Update Caches
        for (UTXORequest utxoRequest : requests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : utxoRequest.getTransactionOutputs()) {
                Data.addUtxo(id, transactionOutput.generateTransactionOutputHash(utxoRequest.getTransactionRequestHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                Data.removeUtxo(id, transactionInput.getTransactionOutputHash());
            }
        }
        Requests.remove(id, requests.getTransactionRequests(), UTXO);
    }

    @Override
    public Optional<UTXORequests> prepareRequests(List<UTXORequest> availableUtxoRequests) {
        Set<String> inputsToInclude = new HashSet<>();
        List<UTXORequest> utxoRequestsToInclude = new ArrayList<>();
        for (UTXORequest utxoRequest : availableUtxoRequests) {
            //verify signature
            boolean verified = UTXOVerification.verifySignature(utxoRequest, false, id);
            if (!verified){
                continue;
            }

            //consider for inclusion in this block.
            List<String> transactionInputsToAdd = new ArrayList<>();
            boolean include = true;
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                //check if input has already been used in this same transaction request
                if (transactionInputsToAdd.contains(transactionInput.getTransactionOutputHash())){
                    include = false;
                    break;
                    //check if input is already added to the set of transactions for this block;
                } else if (inputsToInclude.contains(transactionInput.getTransactionOutputHash())){
                    include = false;
                    break;
                    //check if input is available
                } else if (!Data.hasUtxo(id, transactionInput.getTransactionOutputHash())) {
                    include = false;
                    break;
                } else {
                    transactionInputsToAdd.add(transactionInput.getTransactionOutputHash());
                }
            }
            if (include) {
                utxoRequestsToInclude.add(utxoRequest);
                inputsToInclude.addAll(utxoRequest.getTransactionInputs().stream().map(t -> t.getTransactionOutputHash()).toList());
            }
        }
        if (utxoRequestsToInclude.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new UTXORequests(utxoRequestsToInclude));
        }
    }

    private static boolean checkInputSumEqualToOutputSum(UTXORequest transactionRequest, String id) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = Data.getUtxo(id, transactionInput.getTransactionOutputHash());
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

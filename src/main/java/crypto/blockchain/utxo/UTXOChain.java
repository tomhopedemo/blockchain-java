package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

public record UTXOChain(String id){

    public void genesis(long value, String genesisKey) {
        UTXORequest genesisTransactionRequest = UTXORequestFactory.genesisTransaction(genesisKey, value, id);
        mineNextBlock(new UTXORequests(List.of(genesisTransactionRequest)), id, 1);
    }

    public void simulate(Wallet genesis) {
        Blockchain blockchain = Data.getChain(id);
        Wallet wallet = Wallet.generate();
        List<UTXORequest> utxoRequestsQueue = new ArrayList<>();
        //can i add the requests to a queue in the blockchain itself? - data plez

        Optional<UTXORequest> utxoRequestOptional = UTXORequestFactory.createUTXORequest(wallet, genesis.getPublicKeyAddress(), 5, id);
        if (utxoRequestOptional.isPresent()){
            UTXORequest utxoRequest = utxoRequestOptional.get();
            utxoRequestsQueue.add(utxoRequest);
        }

        if (!utxoRequestsQueue.isEmpty()) {
            Optional<UTXORequests> utxoRequestsForNextBlock = constructUTXORequestsForNextBlock(utxoRequestsQueue, id);
            if (utxoRequestsForNextBlock.isPresent()) {
                mineNextBlock(utxoRequestsForNextBlock.get(), id, 1);
                utxoRequestsQueue.removeAll(utxoRequestsForNextBlock.get().getTransactionRequests());
            }
        }
        Data.addWallet(blockchain.getId(), wallet);
    }

    public static Optional<UTXORequests> constructUTXORequestsForNextBlock(List<UTXORequest> availableUtxoRequests, String id) {
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

    public void mineNextBlock(UTXORequests transactionRequests, String id, int difficulty) {
        Blockchain blockchain = Data.getChain(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null; //indicative of genesis block - will tidy

        //Individual Transaction Verification
        for (UTXORequest transactionRequest : transactionRequests.getTransactionRequests()) {
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
        for (UTXORequest transactionRequest : transactionRequests.getTransactionRequests()) {
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
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (UTXORequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                Data.addUtxo(id, transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                Data.removeUtxo(id, transactionInput.getTransactionOutputHash());
            }
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

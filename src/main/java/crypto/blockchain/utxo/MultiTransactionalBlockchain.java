package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.Data;
import crypto.blockchain.api.BlockchainType;

import java.util.*;

public class MultiTransactionalBlockchain {

    public static void create(String id){
        Data.addBlockchain(new Blockchain(id));
        Data.addTransactionCache(id);
        Data.addWalletCache(id);
    }

    public static void genesis(String id, long value, String genesisKey) throws BlockchainException {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        TransactionRequest genesisTransactionRequest = TransactionRequestFactory.genesisTransaction(genesisKey, value, transactionCache);
        mineNextBlock(new TransactionRequests(List.of(genesisTransactionRequest)), id, 1);
    }

    public static void simulate(String id, int numBlocks, int difficulty) {
        Blockchain blockchain = Data.getBlockchain(id);
        Wallet wallet = Wallet.generate();
        Wallet genesis = Data.getGenesisWallet(id);
        List<TransactionRequest> transactionRequestsQueue = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            createAndRegisterSimpleTransactionRequest(genesis, wallet, transactionRequestsQueue, 5, id);
            if (transactionRequestsQueue.isEmpty()) {
                break;
            }
            Optional<TransactionRequests> transactionRequestsForNextBlock = constructTransactionRequestsForNextBlock(transactionRequestsQueue, id);
            if (transactionRequestsForNextBlock.isPresent()) {
                mineNextBlock(transactionRequestsForNextBlock.get(), id, difficulty);
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }
        Data.addWallet(blockchain.getId(), wallet);
    }

    private static void createAndRegisterSimpleTransactionRequest(Wallet walletA, Wallet walletB, List<TransactionRequest> transactionRequestsQueue, int value, String id) {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        Optional<TransactionRequest> transactionRequestOptional = TransactionRequestFactory.createTransactionRequest(walletA, walletB.publicKeyAddress, value, transactionCache);
        if (transactionRequestOptional.isPresent()){
            TransactionRequest transactionRequest = transactionRequestOptional.get();
            transactionRequestsQueue.add(transactionRequest);
        }
    }

    public static Optional<TransactionRequests> constructTransactionRequestsForNextBlock(List<TransactionRequest> availableTransactionRequests, String id) {
        Set<String> inputsToInclude = new HashSet<>();
        List<TransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        TransactionCache transactionCache = Data.getTransactionCache(id);
        for (TransactionRequest transactionRequest : availableTransactionRequests) {
            //verify signature
            boolean verified = TransactionVerification.verifySignature(transactionRequest, false, id);
            if (!verified){
                continue;
            }

            //consider for inclusion in this block.
            List<String> transactionInputsToAdd = new ArrayList<>();
            boolean include = true;
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                //check if input has already been used in this same transaction request
                if (transactionInputsToAdd.contains(transactionInput.getTransactionOutputHash())){
                    include = false;
                    break;
                    //check if input is already added to the set of transactions for this block;
                } else if (inputsToInclude.contains(transactionInput.getTransactionOutputHash())){
                    include = false;
                    break;
                    //check if input is available
                } else if (!transactionCache.contains(transactionInput.getTransactionOutputHash())) {
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
        if (transactionRequestsToInclude.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new TransactionRequests(transactionRequestsToInclude));
        }
    }

    public static void mineNextBlock(TransactionRequests transactionRequests, String id, int difficulty) {
        Blockchain blockchain = Data.getBlockchain(id);
        TransactionCache transactionCache = Data.getTransactionCache(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean skipEqualityCheck = mostRecentBlock == null; //indicative of genesis block - will tidy

        //Individual Transaction Verification
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            boolean verified = TransactionVerification.verifySignature(transactionRequest, skipEqualityCheck, id);
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
        BlockMiner.mineBlockHash(block, "0".repeat(difficulty));
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

    private static boolean checkInputSumEqualToOutputSum(TransactionRequest transactionRequest, String id) {
        TransactionCache transactionCache = Data.getTransactionCache(id);
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = transactionCache.get(transactionInput.getTransactionOutputHash());
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

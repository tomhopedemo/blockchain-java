package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

public record AccountChain(String id){

    public void genesis(long value, String genesisKey) {
        TransactionOutput transactionOutput = new TransactionOutput(genesisKey, value);
        AccountTransactionRequests requests = new AccountTransactionRequests(List.of(new AccountTransactionRequest(null, List.of(transactionOutput))));
        mineNextBlock(requests, id);
    }

    public void simulate() throws BlockchainException {
        Blockchain blockchain = Data.getChain(id);
        Wallet wallet = Wallet.generate();
        Wallet genesis = Data.getGenesisWallet(id);

        List<AccountTransactionRequest> transactionRequestsQueue = new ArrayList<>();

        Optional<AccountTransactionRequest> transactionRequestOptional = AccountTransactionRequestFactory.createTransactionRequest(genesis, wallet.getPublicKeyAddress(), 5, id);
        if (transactionRequestOptional.isPresent()){
            transactionRequestsQueue.add(transactionRequestOptional.get());
        }

        if (!transactionRequestsQueue.isEmpty()) {
            Optional<AccountTransactionRequests> transactionRequestsForNextBlock = constructTransactionRequestsForNextBlock(transactionRequestsQueue, id);
            if (transactionRequestsForNextBlock.isPresent()) {
                mineNextBlock(transactionRequestsForNextBlock.get(), id);
                transactionRequestsQueue.removeAll(transactionRequestsForNextBlock.get().getTransactionRequests());
            }
        }

        Data.addWallet(id, wallet);
    }

    public void mineNextBlock(AccountTransactionRequests transactionRequests, String id) {
        Blockchain blockchain = Data.getChain(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis =  mostRecentBlock == null;

        //Individual Transaction Verification
        if (!isGenesis) {
            for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
                boolean verified = AccountTransactionVerification.verifySignature(transactionRequest, false, id);
                if (!verified) {
                    return;
                }
            }
        }

        //Overall Verification (no repeat accounts)
        Set<String> accounts = new HashSet<>();
        for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            if (accounts.contains(transactionRequest.getPublicKeyAddress())){
                return;
            }
            accounts.add(transactionRequest.getPublicKeyAddress());
        }


        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);

        //Update Caches
        for (AccountTransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                Data.addAccountBalance(id, transactionOutput.getRecipient(), transactionOutput.getValue());
                if (!isGenesis) {
                    Data.subtractAccountBalance(id, transactionOutput.getRecipient(), transactionOutput.getValue());
                }
            }
        }
    }

    public static Optional<AccountTransactionRequests> constructTransactionRequestsForNextBlock(List<AccountTransactionRequest> availableTransactionRequests, String id) {
        List<AccountTransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (AccountTransactionRequest transactionRequest : availableTransactionRequests) {
            //verify signature
            boolean verified = AccountTransactionVerification.verifySignature(transactionRequest, false, id);
            if (!verified){
                continue;
            }
            transactionRequestsToInclude.add(transactionRequest);
        }
        if (transactionRequestsToInclude.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new AccountTransactionRequests(transactionRequestsToInclude));
        }
    }


}

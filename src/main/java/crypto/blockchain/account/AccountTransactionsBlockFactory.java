package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

public record AccountTransactionsBlockFactory(String id) implements BlockFactory<AccountTransactionRequests, AccountTransactionRequest> {

    @Override
    public void mineNextBlock(AccountTransactionRequests requests) {
        Blockchain blockchain = Data.getChain(id);
        String previousBlockHash = blockchain.getMostRecent() == null ? null : blockchain.getMostRecent().getBlockHashId();

        //Individual Transaction Verification - we can remove this check
        if (blockchain.getMostRecent() != null) {
            for (AccountTransactionRequest request : requests.transactionRequests()) {
                if (!AccountTransactionVerification.verify(request, id)) {
                    return;
                }
            }
        }

        //Overall Verification (no repeat accounts)
        Set<String> accounts = new HashSet<>();
        for (AccountTransactionRequest transactionRequest : requests.transactionRequests()) {
            if (accounts.contains(transactionRequest.publicKey())){
                return;
            }
            accounts.add(transactionRequest.publicKey());
        }


        //Create block
        Block block = new Block(requests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);

        //Update Caches
        for (AccountTransactionRequest transactionRequest : requests.transactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.transactionOutputs()) {
                Data.addAccountBalance(id, transactionOutput.getRecipient(), transactionRequest.currency(), transactionOutput.getValue());
                Data.addAccountBalance(id, transactionRequest.publicKey(), transactionRequest.currency(), -transactionOutput.getValue());
            }
        }
        Requests.remove(id, requests.transactionRequests(), BlockType.ACCOUNT);
    }

    @Override
    public AccountTransactionRequests prepareRequests(List<AccountTransactionRequest> availableRequests) {
        List<AccountTransactionRequest> requests = new ArrayList<>();
        for (AccountTransactionRequest request : availableRequests) {
            boolean verified = AccountTransactionVerification.verify(request, id);
            if (!verified){
                continue;
            }
            requests.add(request);
        }
        return requests.isEmpty() ? null : new AccountTransactionRequests(requests);
    }

}

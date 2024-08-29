package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;

import java.util.*;

public record AccountTransactionsBlockFactory(String id) implements BlockFactory<AccountTransactionRequests, AccountTransactionRequest> {

    @Override
    public void mineNextBlock(AccountTransactionRequests transactionRequests) {
        Blockchain blockchain = Data.getChain(id);
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();
        boolean isGenesis =  mostRecentBlock == null;

        //Individual Transaction Verification
        if (!isGenesis) {
            for (AccountTransactionRequest transactionRequest : transactionRequests.transactionRequests()) {
                boolean verified = AccountTransactionVerification.verify(transactionRequest, id);
                if (!verified) {
                    return;
                }
            }
        }

        //Overall Verification (no repeat accounts)
        Set<String> accounts = new HashSet<>();
        for (AccountTransactionRequest transactionRequest : transactionRequests.transactionRequests()) {
            if (accounts.contains(transactionRequest.publicKey())){
                return;
            }
            accounts.add(transactionRequest.publicKey());
        }


        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner.mineBlockHash(block, "0".repeat(1));
        blockchain.add(block);

        //Update Caches
        for (AccountTransactionRequest transactionRequest : transactionRequests.transactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.transactionOutputs()) {
                Data.addAccountBalance(id, transactionOutput.getRecipient(), transactionOutput.getValue());
                if (!isGenesis) {
                    Data.subtractAccountBalance(id, transactionOutput.getRecipient(), transactionOutput.getValue());
                }
            }
        }
        Requests.remove(id, transactionRequests.transactionRequests(), BlockType.ACCOUNT);
    }

    @Override
    public Optional<AccountTransactionRequests> prepareRequests(List<AccountTransactionRequest> availableAccountTransactionRequests) {
        List<AccountTransactionRequest> transactionRequestsToInclude = new ArrayList<>();
        for (AccountTransactionRequest transactionRequest : availableAccountTransactionRequests) {
            boolean verified = AccountTransactionVerification.verify(transactionRequest, id);
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

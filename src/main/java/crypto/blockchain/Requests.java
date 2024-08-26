package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.utxo.UTXORequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    static Map<String, List<AccountTransactionRequest>> accountRequests;
    static Map<String, List<UTXORequest>> utxoRequests;
    static Map<String, List<DataRequest>> dataRequests;


    public static void add(String id, AccountTransactionRequest request) {
        accountRequests.putIfAbsent(id, new ArrayList<>());
        accountRequests.get(id).add(request);
    }

    public static void add(String id, UTXORequest request) {
        utxoRequests.putIfAbsent(id, new ArrayList<>());
        utxoRequests.get(id).add(request);
    }

    public static void add(String id, DataRequest request) {
        dataRequests.putIfAbsent(id, new ArrayList<>());
        dataRequests.get(id).add(request);
    }

    public static List<? extends Request> get(String id, BlockType blockType) {
        return switch (blockType){
            case DATA -> dataRequests.get(id);
            case ACCOUNT -> accountRequests.get(id);
            case UTXO -> utxoRequests.get(id);
        };
    }

    public static void remove(String id, List<? extends Request> transactionRequests, BlockType blockType) {
        switch (blockType){
            case DATA -> dataRequests.get(id).removeAll(transactionRequests);
            case ACCOUNT -> accountRequests.get(id).removeAll(transactionRequests);
            case UTXO -> utxoRequests.get(id).removeAll(transactionRequests);
        }
    }

}

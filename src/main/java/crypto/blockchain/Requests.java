package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.signed.SignedDataRequest;
import crypto.blockchain.utxo.UTXORequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    static Map<String, List<AccountTransactionRequest>> accountRequests;
    static Map<String, List<UTXORequest>> utxoRequests;
    static Map<String, List<DataRequest>> dataRequests;
    static Map<String, List<SignedDataRequest>> signedDataRequests;


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

    public static void add(String id, SignedDataRequest request) {
        signedDataRequests.putIfAbsent(id, new ArrayList<>());
        signedDataRequests.get(id).add(request);
    }


    public static List<? extends Request> get(String id, BlockType blockType) {
        return switch (blockType){
            case DATA -> dataRequests.get(id);
            case SIGNED_DATA -> signedDataRequests.get(id);
            case ACCOUNT -> accountRequests.get(id);
            case UTXO -> utxoRequests.get(id);
        };
    }

    public static void remove(String id, List<? extends Request> requests, BlockType blockType) {
        switch (blockType){
            case DATA -> dataRequests.get(id).removeAll(requests);
            case SIGNED_DATA -> signedDataRequests.get(id).removeAll(requests);
            case ACCOUNT -> accountRequests.get(id).removeAll(requests);
            case UTXO -> utxoRequests.get(id).removeAll(requests);
        }
    }

}

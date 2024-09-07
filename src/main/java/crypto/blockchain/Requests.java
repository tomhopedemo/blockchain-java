package crypto.blockchain;

import crypto.blockchain.account.AccountRequest;
import crypto.blockchain.currency.CurrencyRequest;
import crypto.blockchain.signed.SignedRequest;
import crypto.blockchain.utxo.UTXORequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests {

    static Map<String, List<AccountRequest>> accountRequests = new HashMap<>();
    static Map<String, List<UTXORequest>> utxoRequests = new HashMap<>();
    static Map<String, List<DataRequest>> dataRequests = new HashMap<>();
    static Map<String, List<KeyPair>> keyPairRequests = new HashMap<>();
    static Map<String, List<SignedRequest>> signedDataRequests = new HashMap<>();
    static Map<String, List<CurrencyRequest>> currencyRequests = new HashMap<>();

    private static Map getMap(BlockType blockType) {
        return switch (blockType){
            case DATA -> dataRequests;
            case SIGNED_DATA -> signedDataRequests;
            case CURRENCY -> currencyRequests;
            case KEYPAIR -> keyPairRequests;
            case ACCOUNT -> accountRequests;
            case UTXO -> utxoRequests;
        };
    }

    public static void add(String id, BlockType blockType, Request request) {
        Map requests = getMap(blockType);
        ((List) requests.computeIfAbsent(id, _ -> new ArrayList<>())).add(request);
    }

    public static List<? extends Request> get(String id, BlockType blockType) {
        return (List) getMap(blockType).get(id);
    }

    public static void remove(String id, List<? extends Request> requests, BlockType blockType) {
        List<? extends Request> found = get(id, blockType);
        if (found != null) found.removeAll(requests);
    }

}

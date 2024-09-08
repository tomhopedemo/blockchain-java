package crypto.blockchain;

import crypto.block.account.AccountRequest;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedRequest;
import crypto.block.utxo.UTXORequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests {

    static Map<String, List<AccountRequest>> accountRequests = new HashMap<>();
    static Map<String, List<UTXORequest>> utxoRequests = new HashMap<>();
    static Map<String, List<DataRequest>> dataRequests = new HashMap<>();
    static Map<String, List<Keypair>> keypairRequests = new HashMap<>();
    static Map<String, List<SignedRequest>> signedDataRequests = new HashMap<>();
    static Map<String, List<CurrencyRequest>> currencyRequests = new HashMap<>();

    private static Map getMap(BlockType blockType) {
        return switch (blockType){
            case DATA -> dataRequests;
            case SIGNED_DATA -> signedDataRequests;
            case CURRENCY -> currencyRequests;
            case KEYPAIR -> keypairRequests;
            case ACCOUNT -> accountRequests;
            case UTXO -> utxoRequests;
        };
    }

    public static void add(String id, Request request) {
        BlockType blockType = BlockType.getType(request.getClass());
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

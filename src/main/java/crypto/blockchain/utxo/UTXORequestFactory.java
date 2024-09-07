package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.data.TransactionRequestParams;

import java.util.*;

public class UTXORequestFactory {

    public static UTXORequest createGenesisRequest(String recipientPublicKeyAddress, long transactionValue, String id) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        return new UTXORequest(new ArrayList<>(), transactionOutputs);
    }

    public static UTXORequest createUTXORequest(String id, TransactionRequestParams transactionRequestParams) throws ChainException{
        Optional<KeyPair> keyPair = Data.getKeyPair(id, transactionRequestParams.from());
        if (keyPair.isEmpty()){
            return null;
        }
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(keyPair.get(), id);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < transactionRequestParams.value()) {
            return null;
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] signature = Signing.sign(keyPair.get(), transactionOutputHash);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= transactionRequestParams.value()) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(transactionRequestParams.to(), transactionRequestParams.value()));
        transactionOutputs.add(new TransactionOutput(keyPair.get().getPublicKeyAddress(), total - transactionRequestParams.value()));
        return new UTXORequest(transactionInputs, transactionOutputs);
    }

    private static long getBalance(Map<String, TransactionOutput> transactionOutputsById) {
        return transactionOutputsById.values().stream()
                .map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
    }

    public static Map<String, TransactionOutput> getTransactionOutputsById(KeyPair keyPair, String id) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        UTXOCache utxoCache = Data.getUTXOCache(id);
        if (utxoCache != null) {
            for (Map.Entry<String, TransactionOutput> item : utxoCache.entrySet()) {
                TransactionOutput transactionOutput = item.getValue();
                if (transactionOutput.getRecipient().equals(keyPair.getPublicKeyAddress())) {
                    transactionOutputsById.put(item.getKey(), transactionOutput);
                }
            }
        }
        return transactionOutputsById;
    }
}

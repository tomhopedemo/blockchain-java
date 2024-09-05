package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.blockchain.api.data.TransactionalRequestParams;

import java.util.*;

public class UTXORequestFactory {

    public static UTXORequest createGenesisRequest(String recipientPublicKeyAddress, long transactionValue, String id) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        return new UTXORequest(new ArrayList<>(), transactionOutputs);
    }

    public static Optional<UTXORequest> createUTXORequest(String id, TransactionalRequestParams transactionalRequestParams) throws ChainException{
        Optional<KeyPair> keyPair = Data.getKeyPair(id, transactionalRequestParams.from());
        if (keyPair.isEmpty()){
            return Optional.empty();
        }
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(keyPair.get(), id);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < transactionalRequestParams.value()) {
            return Optional.empty();
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] signature = Signing.sign(keyPair.get(), transactionOutputHash);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= transactionalRequestParams.value()) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(transactionalRequestParams.to(), transactionalRequestParams.value()));
        transactionOutputs.add(new TransactionOutput(keyPair.get().getPublicKeyAddress(), total - transactionalRequestParams.value()));
        UTXORequest transactionRequest = new UTXORequest(transactionInputs, transactionOutputs);
        return Optional.of(transactionRequest);
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

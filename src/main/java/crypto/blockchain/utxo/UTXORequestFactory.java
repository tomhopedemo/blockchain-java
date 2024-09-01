package crypto.blockchain.utxo;

import crypto.blockchain.*;

import java.util.*;

public class UTXORequestFactory {

    public static UTXORequest createGenesisRequest(String recipientPublicKeyAddress, long transactionValue, String id) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        return new UTXORequest(new ArrayList<>(), transactionOutputs);
    }

    public static Optional<UTXORequest> createUTXORequest(KeyPair keyPair, String recipientPublicKeyAddress, long transactionValue, String id) throws ChainException{
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(keyPair, id);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < transactionValue) {
            return Optional.empty();
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] signature = Signing.sign(keyPair, transactionOutputHash);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= transactionValue) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        transactionOutputs.add(new TransactionOutput(keyPair.getPublicKeyAddress(), total - transactionValue));
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

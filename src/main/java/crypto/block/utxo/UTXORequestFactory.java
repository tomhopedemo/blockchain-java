package crypto.block.utxo;

import crypto.blockchain.*;

import java.util.*;

public class UTXORequestFactory {

    public static UTXORequest createGenesisRequest(String recipientPublicKey, long transactionValue) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKey, transactionValue));
        return new UTXORequest(new ArrayList<>(), transactionOutputs);
    }

    public static UTXORequest createUTXORequest(String id, String from, String to, String currency, Long value) throws ChainException{
        Keypair keypair = Data.getKeypair(id, from);
        if (keypair == null) return null;
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(keypair, id);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < value) {
            return null;
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] signature = Signing.sign(keypair, transactionOutputHash);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= value) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(to, value));
        transactionOutputs.add(new TransactionOutput(keypair.publicKey(), total - value));
        return new UTXORequest(transactionInputs, transactionOutputs);
    }

    private static long getBalance(Map<String, TransactionOutput> transactionOutputsById) {
        return transactionOutputsById.values().stream()
                .map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
    }

    public static Map<String, TransactionOutput> getTransactionOutputsById(Keypair keypair, String id) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        UTXOCache utxoCache = Data.getUTXOCache(id);
        if (utxoCache != null) {
            for (Map.Entry<String, TransactionOutput> item : utxoCache.entrySet()) {
                TransactionOutput transactionOutput = item.getValue();
                if (transactionOutput.getRecipient().equals(keypair.publicKey())) {
                    transactionOutputsById.put(item.getKey(), transactionOutput);
                }
            }
        }
        return transactionOutputsById;
    }
}

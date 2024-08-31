package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UTXORequestFactory {

    public static UTXORequest createGenesisRequest(String recipientPublicKeyAddress, long transactionValue, String id) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        return new UTXORequest(new ArrayList<>(), transactionOutputs);
    }

    public static Optional<UTXORequest> createUTXORequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue, String id) {
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(wallet, id);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < transactionValue) {
            return Optional.empty();
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] preSignature = transactionOutputHash.getBytes(UTF_8);
            byte[] signature;
            try {
                PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
                signature = ECDSA.calculateECDSASignature(privateKey, preSignature);
            } catch (GeneralSecurityException e){
                return Optional.empty();
            }
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= transactionValue) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        transactionOutputs.add(new TransactionOutput(wallet.getPublicKeyAddress(), total - transactionValue));
        UTXORequest transactionRequest = new UTXORequest(transactionInputs, transactionOutputs);
        return Optional.of(transactionRequest);
    }

    private static long getBalance(Map<String, TransactionOutput> transactionOutputsById) {
        return transactionOutputsById.values().stream()
                .map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
    }

    public static Map<String, TransactionOutput> getTransactionOutputsById(Wallet wallet, String id) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        UTXOCache utxoCache = Data.getUTXOCache(id);
        if (utxoCache != null) {
            for (Map.Entry<String, TransactionOutput> item : utxoCache.entrySet()) {
                TransactionOutput transactionOutput = item.getValue();
                if (transactionOutput.getRecipient().equals(wallet.getPublicKeyAddress())) {
                    transactionOutputsById.put(item.getKey(), transactionOutput);
                }
            }
        }
        return transactionOutputsById;
    }
}

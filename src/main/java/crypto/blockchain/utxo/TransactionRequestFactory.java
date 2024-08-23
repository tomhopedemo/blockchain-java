package crypto.blockchain.utxo;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionRequestFactory {

    public static Optional<TransactionRequest> createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue, TransactionCache transactionCache) {
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(wallet, transactionCache);
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
                PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.privateKey);
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
        transactionOutputs.add(new TransactionOutput(wallet.publicKeyAddress, total - transactionValue));
        TransactionRequest transactionRequest = new TransactionRequest(transactionInputs, transactionOutputs);
        return Optional.of(transactionRequest);
    }

    private static long getBalance(Map<String, TransactionOutput> transactionOutputsById) {
        return transactionOutputsById.values().stream()
                .map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
    }

    public static Map<String, TransactionOutput> getTransactionOutputsById(Wallet wallet, TransactionCache transactionCache) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        for (Map.Entry<String, TransactionOutput> item: transactionCache.entrySet()){
            TransactionOutput transactionOutput = item.getValue();
            if (transactionOutput.getRecipient().equals(wallet.publicKeyAddress)) {
                transactionOutputsById.put(item.getKey(), transactionOutput);
            }
        }
        return transactionOutputsById;
    }

    public static TransactionRequest genesisTransaction(String genesisKey, long genesisTransactionValue, TransactionCache transactionCache) {
        TransactionOutput genesisTransactionOutput = new TransactionOutput(genesisKey, genesisTransactionValue);
        List<TransactionOutput> transactionOutputs = List.of(genesisTransactionOutput);
        TransactionRequest genesisTransactionRequest = new TransactionRequest(new ArrayList<>(), transactionOutputs);
        String transactionOutputHash = genesisTransactionOutput.generateTransactionOutputHash(genesisTransactionRequest.getTransactionRequestHash());
        transactionCache.put(transactionOutputHash, genesisTransactionOutput);
        return genesisTransactionRequest;
    }
}

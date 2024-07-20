package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionRequestFactory {

    WalletStore walletStore;
    TransactionCache transactionCache;

    public TransactionRequestFactory(WalletStore walletStore, TransactionCache transactionCache) {
        this.walletStore = walletStore;
        this.transactionCache = transactionCache;
    }

    public TransactionRequest sendFunds(Wallet wallet, PublicKey recipient, long transactionValue) throws Exception {
        Map<String, TransactionOutput> transactionOutputsById = getTransactionOutputsById(wallet);
        long balance = transactionOutputsById.values().stream().map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
        if (balance < transactionValue) {
            throw new Exception();
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: transactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] preSignature = transactionOutputHash.getBytes(UTF_8);
            byte[] signature = ECDSA.calculateECDSASignature(wallet.privateKey, preSignature);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total > transactionValue) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipient, transactionValue));
        transactionOutputs.add(new TransactionOutput(wallet.publicKeyAddress, total - transactionValue));
        TransactionRequest transactionRequest = new TransactionRequest(wallet, recipient, transactionValue, transactionInputs, transactionOutputs);
        String transactionRequestHash = Encoder.encodeToHexadecimal(transactionRequest.getHash());

        for (TransactionInput transactionInput : transactionInputs) {
            String transactionOutputHash = transactionInput.getTransactionOutputHash();
            TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
            boolean verified = ECDSA.verifyECDSASignature(transactionOutput.recipient, transactionOutputHash.getBytes(UTF_8), transactionInput.getSignature());
            System.out.println("Input Verification : " + verified);
        }

        for (TransactionOutput transactionOutput : transactionOutputs) {
            transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequestHash), transactionOutput);
        }
        for (TransactionInput transactionInput : transactionInputs) {
            transactionCache.remove(transactionInput.getTransactionOutputHash());
        }
        return transactionRequest;
    }

    public Map<String, TransactionOutput> getTransactionOutputsById(Wallet wallet) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        for (Map.Entry<String, TransactionOutput> item: transactionCache.entrySet()){
            TransactionOutput transactionOutput = item.getValue();
            if (transactionOutput.getRecipient().equals(wallet.publicKeyAddress)) {
                transactionOutputsById.put(item.getKey(), transactionOutput);
            }
        }
        return transactionOutputsById;
    }

    public TransactionRequest genesisTransaction(Wallet genesisWallet, Wallet walletA, long genesisTransactionValue) throws Exception {
        TransactionOutput genesisTransactionOutput = new TransactionOutput(walletA.publicKeyAddress, 100);
        List<TransactionOutput> transactionOutputs = List.of(genesisTransactionOutput);
        TransactionRequest genesisTransactionRequest = new TransactionRequest(genesisWallet, walletA.publicKeyAddress, genesisTransactionValue, new ArrayList<>(), transactionOutputs);
        String transactionRequestHash = Encoder.encodeToHexadecimal(genesisTransactionRequest.getHash());
        String transactionOutputHash = genesisTransactionOutput.generateTransactionOutputHash(transactionRequestHash);
        transactionCache.put(transactionOutputHash, genesisTransactionOutput);
        return genesisTransactionRequest;
    }
}

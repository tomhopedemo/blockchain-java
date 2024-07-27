package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


//next implementation will be to allow multiple transactions per block with the blockhash on the
//group of transactions.
public class TransactionRequestFactory {

    WalletStore walletStore;
    TransactionCache transactionCache;

    public TransactionRequestFactory(WalletStore walletStore, TransactionCache transactionCache) {
        this.walletStore = walletStore;
        this.transactionCache = transactionCache;
    }

    public TransactionRequest createTransactionRequest(Wallet wallet, String recipientPublicKeyAddress, long transactionValue) throws Exception {
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
            byte[] signature = ECDSA.calculateECDSASignature(Encoder.decodeToPrivateKey(wallet.privateKey), preSignature);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= transactionValue) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKeyAddress, transactionValue));
        transactionOutputs.add(new TransactionOutput(wallet.publicKeyAddress, total - transactionValue));
        TransactionRequest transactionRequest = new TransactionRequest(transactionInputs, transactionOutputs);
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

    public TransactionRequest genesisTransaction(Wallet walletA, long genesisTransactionValue) throws Exception {
        TransactionOutput genesisTransactionOutput = new TransactionOutput(walletA.publicKeyAddress, genesisTransactionValue);
        List<TransactionOutput> transactionOutputs = List.of(genesisTransactionOutput);
        TransactionRequest genesisTransactionRequest = new TransactionRequest(new ArrayList<>(), transactionOutputs);
        String transactionOutputHash = genesisTransactionOutput.generateTransactionOutputHash(genesisTransactionRequest.getTransactionRequestHash());
        transactionCache.put(transactionOutputHash, genesisTransactionOutput);
        return genesisTransactionRequest;
    }
}

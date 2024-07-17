package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transfer {

    TransactionCache transactionCache = new TransactionCache();
    WalletStore walletStore;

    public Transfer(WalletStore walletStore) {
        this.walletStore = walletStore;
    }

    public void sendFunds(Wallet wallet, PublicKey recipient, long transactionValue) throws Exception {
        Map<String, TransactionOutput> transactionOutputsById = getTransactionOutputsById(wallet);
        long balance = transactionOutputsById.values().stream().map(to -> to.value).mapToLong(Long::longValue).sum();
        if (balance < transactionValue) {
            throw new BalanceException();
        }

        List<String> inputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: transactionOutputsById.entrySet()){
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.value;
            inputs.add(transactionOutput.id);
            if (total > transactionValue) break;
        }

        TransactionRequest transactionRequest = new TransactionRequest(wallet, recipient, transactionValue, inputs);
        String transactionRequestHashId = transactionRequest.calulateHash();

        String outputMainId = ECDSA.applySha256HexadecimalEncoding(Encoder.encode(recipient) + transactionValue + transactionRequestHashId);
        String outputLeftOverId = ECDSA.applySha256HexadecimalEncoding(Encoder.encode(wallet.publicKeyAddress) + (total - transactionValue) + transactionRequestHashId);

        TransactionOutput outputMain = new TransactionOutput(outputMainId, recipient, transactionValue);
        TransactionOutput outputLeftOver = new TransactionOutput(outputLeftOverId, wallet.publicKeyAddress, total - transactionValue);
        transactionCache.put(outputMain.id, outputMain);
        transactionCache.put(outputLeftOver.id, outputLeftOver);

        for(String transactionId : inputs) {
            transactionCache.remove(transactionId);
        }
    }

    public Map<String, TransactionOutput> getTransactionOutputsById(Wallet wallet) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        for (Map.Entry<String, TransactionOutput> item: transactionCache.entrySet()){
            TransactionOutput transactionOutput = item.getValue();
            if(transactionOutput.isMine(wallet.publicKeyAddress)) {
                transactionOutputsById.put(transactionOutput.id, transactionOutput);
            }
        }
        return transactionOutputsById;
    }

}

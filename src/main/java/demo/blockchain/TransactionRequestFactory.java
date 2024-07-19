package demo.blockchain;

import demo.encoding.Encoder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRequestFactory {

    TransactionCache transactionCache;
    WalletStore walletStore;

    public TransactionRequestFactory(WalletStore walletStore, TransactionCache transactionCache) {
        this.walletStore = walletStore;
        this.transactionCache = transactionCache;
    }

    public TransactionRequest sendFunds(Wallet wallet, PublicKey recipient, long transactionValue) throws Exception {
        Map<String, TransactionOutput> transactionOutputsById = getTransactionOutputsById(wallet);
        long balance = transactionOutputsById.values().stream().map(to -> to.value()).mapToLong(Long::longValue).sum();
        if (balance < transactionValue) {
            throw new Exception();
        }

        List<String> inputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: transactionOutputsById.entrySet()){
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.value();
            inputs.add(transactionOutput.id());
            if (total > transactionValue) break;
        }

        TransactionRequest transactionRequest = new TransactionRequest(wallet, recipient, transactionValue, inputs);
        String transactionRequestHashId = constructTransactionRequestHashId(transactionValue, transactionRequest);

        String outputMainId = Sha256Tools.applySha256HexadecimalEncoding(Encoder.encode(recipient) + transactionValue + transactionRequestHashId);
        String outputLeftOverId = Sha256Tools.applySha256HexadecimalEncoding(Encoder.encode(wallet.publicKeyAddress) + (total - transactionValue) + transactionRequestHashId);

        TransactionOutput outputMain = new TransactionOutput(outputMainId, recipient, transactionValue);
        TransactionOutput outputLeftOver = new TransactionOutput(outputLeftOverId, wallet.publicKeyAddress, total - transactionValue);

        transactionCache.put(outputMain.id(), outputMain);
        transactionCache.put(outputLeftOver.id(), outputLeftOver);

        for(String transactionId : inputs) {
            transactionCache.remove(transactionId);
        }
        return transactionRequest;
    }

    public static String constructTransactionRequestHashId(long transactionValue, TransactionRequest transactionRequest) throws Exception {
        String transactionRequestHashId = Sha256Tools.applySha256HexadecimalEncoding(
                        Encoder.encode(transactionRequest.senderPublicKeyAddress) +
                        Encoder.encode(transactionRequest.recipientPublicKeyAddress) + transactionValue +
                        String.join("", transactionRequest.inputTransactionOutputIds)
        );
        return transactionRequestHashId;
    }

    public Map<String, TransactionOutput> getTransactionOutputsById(Wallet wallet) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        for (Map.Entry<String, TransactionOutput> item: transactionCache.entrySet()){
            TransactionOutput transactionOutput = item.getValue();
            if(transactionOutput.recipient().equals(wallet.publicKeyAddress)) {
                transactionOutputsById.put(transactionOutput.id(), transactionOutput);
            }
        }
        return transactionOutputsById;
    }

    public TransactionRequest genesisTransaction(Wallet genesisWallet, Wallet walletA, long genesisTransactionValue) throws Exception {
        TransactionRequest genesisTransactionRequest = new TransactionRequest(genesisWallet, walletA.publicKeyAddress, genesisTransactionValue, new ArrayList<>());
        String transactionRequestHashId = TransactionRequestFactory.constructTransactionRequestHashId(genesisTransactionValue, genesisTransactionRequest);
        String genesisTransactionOutputId = Sha256Tools.applySha256HexadecimalEncoding(Encoder.encode(walletA.publicKeyAddress) + genesisTransactionValue + transactionRequestHashId);
        TransactionOutput genesisTransactionOutput = new TransactionOutput(genesisTransactionOutputId, walletA.publicKeyAddress, 100);
        transactionCache.put(genesisTransactionOutputId, genesisTransactionOutput);
        return genesisTransactionRequest;
    }
}

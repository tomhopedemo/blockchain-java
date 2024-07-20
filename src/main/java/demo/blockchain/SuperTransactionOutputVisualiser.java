package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import demo.encoding.Encoder;

import java.util.stream.Collectors;

public class SuperTransactionOutputVisualiser {

    public TransactionCache transactionCache;

    public SuperTransactionOutputVisualiser(TransactionCache transactionCache) {
        this.transactionCache = transactionCache;
    }

    public void visualise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(transactionCache.values().stream().map(transactionOutput -> new TransactionOutputVisualised(transactionOutput)).collect(Collectors.toList()));
        System.out.println(json);
    }

    //modify wallets visualisation to json

    static class TransactionOutputVisualised {
        public String transactionOutputValue;
        public String recipientEncoded;

        public TransactionOutputVisualised(TransactionOutput transactionOutput) {
            this.transactionOutputValue = String.valueOf(transactionOutput.getValue());
            this.recipientEncoded = transactionOutput.getRecipient();
        }
    }


}

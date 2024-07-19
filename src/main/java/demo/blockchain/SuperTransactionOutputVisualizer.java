package demo.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import demo.encoding.Encoder;

import java.util.stream.Collectors;

public class SuperTransactionOutputVisualizer {

    public TransactionCache transactionCache;

    public SuperTransactionOutputVisualizer(TransactionCache transactionCache) {
        this.transactionCache = transactionCache;
    }

    public void visualise(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(transactionCache.values().stream().map(transactionOutput -> new TransactionOutputVisualised(transactionOutput)).collect(Collectors.toList()));
        System.out.println(json);
    }

    static class TransactionOutputVisualised {
        public String id;
        public String transactionOutputValue;
        public String recipientEncoded;

        public TransactionOutputVisualised(TransactionOutput transactionOutput) {
            this.id = transactionOutput.id();
            this.transactionOutputValue = String.valueOf(transactionOutput.value());
            this.recipientEncoded = Encoder.encode(transactionOutput.recipient());
        }
    }


}

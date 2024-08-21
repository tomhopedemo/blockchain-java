package demo.blockchain.account;

import demo.blockchain.BlockDataHashable;
import demo.encoding.Encoder;
import demo.hashing.Hashing;

import java.util.List;

public class AccountBasedTransactionRequests implements BlockDataHashable {

    List<AccountBasedTransactionRequest> transactionRequests;

    public AccountBasedTransactionRequests(List<AccountBasedTransactionRequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    public List<AccountBasedTransactionRequest> getTransactionRequests() {
        return transactionRequests;
    }

    @Override
    public String blockDataHash()  {
        String preHash = String.join("", transactionRequests.stream().map(transactionRequest -> transactionRequest.getTransactionRequestHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }
}

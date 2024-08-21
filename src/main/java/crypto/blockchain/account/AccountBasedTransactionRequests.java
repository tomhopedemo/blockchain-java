package crypto.blockchain.account;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

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

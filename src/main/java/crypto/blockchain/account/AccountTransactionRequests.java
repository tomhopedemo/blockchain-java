package crypto.blockchain.account;

import crypto.blockchain.BlockDataHashable;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.util.List;

public class AccountTransactionRequests implements BlockDataHashable {

    List<AccountTransactionRequest> transactionRequests;

    public AccountTransactionRequests(List<AccountTransactionRequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    public List<AccountTransactionRequest> getTransactionRequests() {
        return transactionRequests;
    }

    @Override
    public String getBlockDataHash()  {
        String preHash = String.join("", transactionRequests.stream().map(transactionRequest -> transactionRequest.getBlockDataHash()).toList());
        byte[] hash = Hashing.hash(preHash);
        return Encoder.encodeToHexadecimal(hash);
    }
}

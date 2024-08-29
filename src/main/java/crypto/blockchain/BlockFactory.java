package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.account.AccountTransactionRequests;

import java.util.List;
import java.util.Optional;

public interface BlockFactory<B extends BlockDataHashable, R extends Request> {

    void mineNextBlock(B b);

    Optional<B> prepareRequests(List<R> requests);

}
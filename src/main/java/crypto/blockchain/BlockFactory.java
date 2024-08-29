package crypto.blockchain;

import java.util.List;

public interface BlockFactory<B extends BlockDataHashable, R extends Request> {

    void mineNextBlock(B b);

    B prepareRequests(List<R> requests);

}
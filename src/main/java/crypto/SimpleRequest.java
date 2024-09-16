package crypto;

import java.util.ArrayList;
import java.util.List;

public interface SimpleRequest<R extends Request> extends Request<R> {

    @Override
    default BlockData<R> prepare(String id, List<R> requests){
        return new BlockData<>(new ArrayList<>(requests));
    }

    @Override
    default boolean verify(String id, R request){
        return true;
    }


}

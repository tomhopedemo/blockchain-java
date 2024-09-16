package crypto;

import java.util.ArrayList;
import java.util.List;

public interface SimpleBlockFactory<R extends Request> {

    default BlockData<R> prepare(String id, List<R> requests){
        return new BlockData<>(new ArrayList<>(requests));
    }

    default boolean verify(String id, R request){
        return true;
    }
}

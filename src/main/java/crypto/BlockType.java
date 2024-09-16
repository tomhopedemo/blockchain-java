package crypto;

import crypto.block.*;
import crypto.block.Currency;

import java.util.*;

public enum BlockType {

    ACCOUNT(Account.class),
    CURRENCY(Currency.class),
    DATA(Data.class),
    DIFFICULTY(Difficulty.class),
    KEYPAIR(Keypair.class),
    PUBLISH(Publish.class),
    SIGNED(Signed.class),
    STAKE(Stake.class),
    UTXO(crypto.block.utxo.UTXO.class);

    private final Class<? extends Request> requestClass;
    private final Map<String, List<? extends Request>> requestMap;

    BlockType(Class<? extends Request> requestClass) {
        this.requestClass = requestClass;
        this.requestMap = new HashMap<>();
    }

    public static BlockType getType(Class<? extends Request> requestClass) {
        Optional<BlockType> found = Arrays.stream(BlockType.values()).filter(type -> type.getRequestClass().equals(requestClass)).findFirst();
        return found.isPresent() ? found.get() : null;
    }

    public Class<? extends Request> getRequestClass() {
        return requestClass;
    }

    public Map<String, List<? extends Request>> getRequestMap(){
        return requestMap;
    }
}

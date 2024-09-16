package crypto;

import crypto.block.account.AccountRequest;
import crypto.block.currency.CurrencyRequest;
import crypto.block.data.DataRequest;
import crypto.block.difficulty.DifficultyRequest;
import crypto.block.signed.SignedRequest;
import crypto.block.stake.StakeRequest;
import crypto.block.utxo.UTXORequest;

import java.util.*;

public enum BlockType {

    ACCOUNT(AccountRequest.class),
    CURRENCY(CurrencyRequest.class),
    DATA(DataRequest.class),
    DIFFICULTY(DifficultyRequest.class),
    KEYPAIR(Keypair.class),
    SIGNED(SignedRequest.class),
    STAKE(StakeRequest.class),
    UTXO(UTXORequest.class);

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

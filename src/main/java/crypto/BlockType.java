package crypto;

import crypto.block.account.AccountFactory;
import crypto.block.account.AccountRequest;
import crypto.block.currency.CurrencyFactory;
import crypto.block.currency.CurrencyRequest;
import crypto.block.data.DataFactory;
import crypto.block.data.DataRequest;
import crypto.block.difficulty.DifficultyFactory;
import crypto.block.difficulty.DifficultyRequest;
import crypto.block.keypair.KeypairFactory;
import crypto.block.signed.SignedFactory;
import crypto.block.signed.SignedRequest;
import crypto.block.stake.StakeFactory;
import crypto.block.stake.StakeRequest;
import crypto.block.utxo.UTXOFactory;
import crypto.block.utxo.UTXORequest;

import java.util.*;

public enum BlockType {

    ACCOUNT(AccountRequest.class, AccountFactory.class),
    CURRENCY(CurrencyRequest.class, CurrencyFactory.class),
    DATA(DataRequest.class, DataFactory.class),
    DIFFICULTY(DifficultyRequest.class, DifficultyFactory.class),
    KEYPAIR(Keypair.class, KeypairFactory.class),
    SIGNED(SignedRequest.class, SignedFactory.class),
    STAKE(StakeRequest.class, StakeFactory.class),
    UTXO(UTXORequest.class, UTXOFactory.class);

    private final Class<? extends Request> requestClass;
    private final Class<? extends BlockFactory> factoryClass;
    private final Map<String, List<? extends Request>> requestMap;

    BlockType(Class<? extends Request> requestClass, Class<? extends BlockFactory> factoryClass) {
        this.requestClass = requestClass;
        this.factoryClass = factoryClass;
        this.requestMap = new HashMap<>();
    }

    public static BlockType getType(Class<? extends Request> requestClass) {
        Optional<BlockType> found = Arrays.stream(BlockType.values()).filter(type -> type.getRequestClass().equals(requestClass)).findFirst();
        return found.isPresent() ? found.get() : null;
    }

    public Class<? extends Request> getRequestClass() {
        return requestClass;
    }
    public Class<? extends BlockFactory> getFactoryClass() {
        return factoryClass;
    }

    public Map<String, List<? extends Request>> getRequestMap(){
        return requestMap;
    }
}

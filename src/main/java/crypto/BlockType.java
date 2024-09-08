package crypto;

import crypto.block.account.AccountRequest;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedRequest;
import crypto.block.utxo.UTXORequest;

import java.util.Arrays;
import java.util.Optional;

public enum BlockType {
    DATA(DataRequest.class),
    SIGNED_DATA(SignedRequest.class),
    CURRENCY(CurrencyRequest.class),
    KEYPAIR(Keypair.class),
    ACCOUNT(AccountRequest.class),
    UTXO(UTXORequest.class);

    private final Class<? extends Request> requestClass;

    BlockType(Class<? extends Request> requestClass) {
        this.requestClass = requestClass;
    }

    public static BlockType getType(Class<? extends Request> requestClass) {
        Optional<BlockType> found = Arrays.stream(BlockType.values()).filter(type -> type.getRequestClass().equals(requestClass)).findFirst();
        return found.isPresent() ? found.get() : null;
    }

    public Class<? extends Request> getRequestClass() {
        return requestClass;
    }
}

package crypto.blockchain;

import crypto.block.account.AccountRequest;
import crypto.block.currency.CurrencyRequest;
import crypto.block.signed.SignedRequest;
import crypto.block.utxo.UTXORequest;

public enum BlockType {
    DATA(DataRequest.class),
    SIGNED_DATA(SignedRequest.class),
    CURRENCY(CurrencyRequest.class),
    KEYPAIR(KeyPair.class),
    ACCOUNT(AccountRequest.class),
    UTXO(UTXORequest.class);

    private final Class<? extends Request> requestClass;

    BlockType(Class<? extends Request> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends Request> getRequestClass() {
        return requestClass;
    }
}

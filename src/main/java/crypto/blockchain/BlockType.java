package crypto.blockchain;

import crypto.blockchain.account.AccountRequest;
import crypto.blockchain.signed.SignedRequest;
import crypto.blockchain.utxo.UTXORequest;

public enum BlockType {
    DATA(DataRequest.class),
    SIGNED_DATA(SignedRequest.class),
    CURRENCY(CurrencyRequest.class),
    ACCOUNT(AccountRequest.class),
    UTXO(UTXORequest.class);

    private final Class<?> requestClass;

    BlockType(Class<?> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<?> getRequestClass() {
        return requestClass;
    }
}

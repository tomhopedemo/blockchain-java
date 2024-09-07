package crypto.blockchain;

import crypto.blockchain.account.AccountRequest;
import crypto.blockchain.currency.CurrencyRequest;
import crypto.blockchain.signed.SignedRequest;
import crypto.blockchain.utxo.UTXORequest;

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

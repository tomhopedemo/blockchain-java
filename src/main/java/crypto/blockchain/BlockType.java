package crypto.blockchain;

import crypto.blockchain.account.AccountTransactionRequest;
import crypto.blockchain.signed.SignedDataRequest;
import crypto.blockchain.utxo.UTXORequest;

public enum BlockType {
    DATA(DataRequest.class),
    SIGNED_DATA(SignedDataRequest.class),
    ACCOUNT(AccountTransactionRequest.class),
    UTXO(UTXORequest.class);

    private final Class<?> requestClass;

    BlockType(Class<?> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<?> getRequestClass() {
        return requestClass;
    }
}

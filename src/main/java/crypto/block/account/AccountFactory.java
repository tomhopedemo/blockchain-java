package crypto.block.account;

import crypto.blockchain.*;
import crypto.blockchain.Data;
import crypto.blockchain.api.data.TransactionRequestParams;
import crypto.blockchain.BlockData;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public record AccountFactory(String id) implements BlockFactory<AccountRequest> {

    @Override
    public void mine(BlockData<AccountRequest> blockData) {
        if (!verify(blockData)) return;
        if (blockData.data().size() != blockData.data().stream().map(r -> r.publicKey()).distinct().toList().size()) return;
        addBlock(id, blockData);
        for (AccountRequest request : blockData.data()) {
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                Data.addAccountBalance(id, transactionOutput.getRecipient(), request.currency(), transactionOutput.getValue());
                Data.addAccountBalance(id, request.publicKey(), request.currency(), -transactionOutput.getValue());
            }
        }
        Requests.remove(id, blockData.data(), BlockType.ACCOUNT);
    }

    @Override
    public BlockData<AccountRequest> prepare(List<AccountRequest> requests) {
        List<AccountRequest> selected = new ArrayList<>();
        for (AccountRequest request : requests) {
            if (!verify(request)) continue;
            selected.add(request);
        }
        return selected.isEmpty() ? null : new BlockData<>(selected);
    }

    @Override
    public boolean verify(AccountRequest request) {
        try {
            PublicKey publicKey = Encoder.decodeToPublicKey(request.publicKey());
            String hash = AccountRequest.generateHash(request.publicKey(), request.currency(), request.transactionOutputs());
            if (!ECDSA.verifyECDSASignature(publicKey, hash.getBytes(UTF_8), Hex.decode(request.signature()))) return false;
        } catch (GeneralSecurityException e){
            return false;
        }

        if (Data.hasAccountCache(id, request.currency())) {
            long sum = 0L;
            for (TransactionOutput transactionOutput : request.transactionOutputs()) {
                sum += transactionOutput.getValue();
            }
            Long balance = Data.getAccountBalance(id, request.currency(), request.publicKey());
            if (!(balance >= sum)) return false;
        }
        return true;
    }

    public AccountRequest create(TransactionRequestParams params) throws ChainException {
        KeyPair keyPair = Data.getKeyPair(id, params.from());
        if (keyPair == null) return null;
        Long balance = Data.getAccountBalance(id, params.currency(), keyPair.publicKey());
        if (balance < params.value()) return null;
        List<TransactionOutput> transactionOutputs = List.of(new TransactionOutput(params.to(), params.value()));
        return create(keyPair, params.currency(), transactionOutputs);
    }

    private AccountRequest create(KeyPair keyPair, String currency, List<TransactionOutput> transactionOutputs) throws ChainException {
        String hash = AccountRequest.generateHash(keyPair.publicKey(), currency, transactionOutputs);
        byte[] signature = Signing.sign(keyPair, hash);
        return new AccountRequest(keyPair.publicKey(), currency, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }
}

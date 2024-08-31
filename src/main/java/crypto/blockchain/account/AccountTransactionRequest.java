package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record AccountTransactionRequest (String publicKey, List<TransactionOutput> transactionOutputs, String signature) implements BlockDataHashable, Request {

    @Override
    public String getBlockDataHash() {
        byte[] hash = Hashing.hash(signature);
        return Encoder.encodeToHexadecimal(hash);
    }

    private static String generateIntegratedHash(String publicKeyAddress, List<TransactionOutput> transactionOutputs) {
        String simplePreHash = publicKeyAddress +
                String.join("", transactionOutputs.stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        String simpleHash = Encoder.encodeToHexadecimal(Hashing.hash(simplePreHash));

        String integratedPreHash = String.join("", transactionOutputs.stream().map(output -> output.generateTransactionOutputHash(simpleHash)).toList());
        byte[] integratedHash = Hashing.hash(integratedPreHash);
        return Encoder.encodeToHexadecimal(integratedHash);
    }

    public static AccountTransactionRequest create(Wallet wallet, List<TransactionOutput> transactionOutputs) throws ChainException {
        String publicKeyAddress = wallet.getPublicKeyAddress();
        String integratedHash = generateIntegratedHash(wallet.getPublicKeyAddress(), transactionOutputs);
        byte[] signature = Signing.sign(wallet, integratedHash);
        return new AccountTransactionRequest(publicKeyAddress, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }

    public String generateIntegratedHash(){
        return generateIntegratedHash(publicKey, transactionOutputs);
    }
}

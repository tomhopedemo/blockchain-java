package crypto.blockchain.account;

import crypto.blockchain.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public record AccountTransactionRequest (String publicKeyAddress, List<TransactionOutput> transactionOutputs, String signature) implements BlockDataHashable, Request {

    @Override
    public String getBlockDataHash() {
        byte[] hash = Hashing.hash(signature);
        return Encoder.encodeToHexadecimal(hash);
    }

    public static byte[] calculateSignature(Wallet wallet, List<TransactionOutput> transactionOutputs) throws ChainException {
        String integratedHash = generateIntegratedHash(wallet.getPublicKeyAddress(), transactionOutputs);
        byte[] preSignature = integratedHash.getBytes(UTF_8);
        try {
            PrivateKey privateKey = Encoder.decodeToPrivateKey(wallet.getPrivateKey());
            return ECDSA.calculateECDSASignature(privateKey, preSignature);
        } catch (GeneralSecurityException e){
            throw new ChainException(e);
        }
    }

    private static String generateIntegratedHash(String publicKeyAddress, List<TransactionOutput> transactionOutputs) {
        String simplePreHash = publicKeyAddress +
                String.join("", transactionOutputs.stream().map(transactionOutput -> transactionOutput.serialise()).toList());
        String simpleHash = Encoder.encodeToHexadecimal(Hashing.hash(simplePreHash));

        String integratedPreHash = String.join("", transactionOutputs.stream().map(output -> output.generateTransactionOutputHash(simpleHash)).toList());
        byte[] integratedHash = Hashing.hash(integratedPreHash);
        return Encoder.encodeToHexadecimal(integratedHash);
    }


    public AccountTransactionRequest(String publicKeyAddress, List<TransactionOutput> transactionOutputs, String signature) {
        this.publicKeyAddress = publicKeyAddress;
        this.transactionOutputs = transactionOutputs;
        this.signature = signature;
    }

    public static AccountTransactionRequest create(Wallet wallet, List<TransactionOutput> transactionOutputs) throws ChainException {
        String publicKeyAddress = wallet.getPublicKeyAddress();
        byte[] signature = calculateSignature(wallet, transactionOutputs);
        return new AccountTransactionRequest(publicKeyAddress, transactionOutputs, Encoder.encodeToHexadecimal(signature));
    }

    public static AccountTransactionRequest createGenesis(List<TransactionOutput> transactionOutputs){
        return new AccountTransactionRequest("", transactionOutputs, "");
    }


    public String generateIntegratedHash(){
        return generateIntegratedHash(publicKeyAddress, transactionOutputs);
    }

}

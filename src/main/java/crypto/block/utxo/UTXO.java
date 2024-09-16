package crypto.block.utxo;

import crypto.*;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static crypto.BlockType.UTXO;
import static java.nio.charset.StandardCharsets.UTF_8;

public class UTXO implements Request<crypto.block.utxo.UTXO> {

    public List<TransactionInput> transactionInputs;
    public List<TransactionOutput> transactionOutputs;

    @Override
    public String getPreHash(){
        return String.join("", getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()) +
                String.join("", getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList());
    }

    public UTXO(List<TransactionInput> transactionInputs, List<TransactionOutput> transactionOutputs) {
        this.transactionInputs = transactionInputs;
        this.transactionOutputs = transactionOutputs;
    }

    public List<TransactionOutput> getTransactionOutputs() {
        return transactionOutputs;
    }

    public List<TransactionInput> getTransactionInputs() {
        return transactionInputs;
    }



    @Override
    public void mine(String id, BlockData<crypto.block.utxo.UTXO> requests) {
        if (Caches.getChain(id).getMostRecentHash() != null) {
            for (crypto.block.utxo.UTXO transactionRequest : requests.data()) {
                if (!verify(id, transactionRequest)) return;
                if (!checkInputSumEqualToOutputSum(transactionRequest, id)) return;
            }
        }

        Set<String> inputs = new HashSet<>();
        for (crypto.block.utxo.UTXO utxoRequest : requests.data()) {
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                if (inputs.contains(transactionInput.transactionOutputHash())) return;
                inputs.add(transactionInput.transactionOutputHash());
            }
        }
        addBlock(id, requests);
        for (crypto.block.utxo.UTXO utxoRequest : requests.data()) {
            String currency = utxoRequest.getTransactionOutputs().getFirst().currency();
            for (TransactionOutput transactionOutput : utxoRequest.getTransactionOutputs()) {
                Caches.addUtxo(id, currency, transactionOutput.generateTransactionOutputHash(utxoRequest.getBlockDataHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                Caches.removeUtxo(id, currency, transactionInput.transactionOutputHash());
            }
        }
        Requests.remove(id, requests.data(), UTXO);
    }

    @Override
    public BlockData<crypto.block.utxo.UTXO> prepare(String id, List<crypto.block.utxo.UTXO> requests) {
        Set<String> inputsReferenced = new HashSet<>();
        List<crypto.block.utxo.UTXO> included = new ArrayList<>();
        Blockchain chain = Caches.getChain(id);
        if (chain.getMostRecentHash() == null){
            requests = requests.stream().filter(r -> r.transactionInputs.isEmpty()).toList();
        }

        for (crypto.block.utxo.UTXO request : requests) {
            boolean shouldInclude;
            if (chain.getMostRecentHash() == null){
                shouldInclude = includeGenesisRequest(request);
            } else {
                shouldInclude = includeUtxoRequest(id, request, inputsReferenced);
            }

            if (shouldInclude) {
                included.add(request);
                inputsReferenced.addAll(request.getTransactionInputs().stream().map(t -> t.transactionOutputHash()).toList());
            }
        }
        return included.isEmpty() ? null : new BlockData<>(included);
    }

    @Override
    public boolean verify(String id, crypto.block.utxo.UTXO request) {
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.transactionOutputHash();
            TransactionOutput transactionOutput = Caches.getUtxo(id, transactionOutputHash);
            if (transactionOutput == null) return false;
            try {
                PublicKey publicKey = Encoder.decodeToPublicKey(transactionOutput.getRecipient());
                if (!ECDSA.verifyECDSASignature(publicKey, transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.signature()))) return false;
            } catch (GeneralSecurityException e){
                return false;
            }
        }
        if (!isInputSumEqualToOutputSum(id, request)) return false;
        return true;
    }

    private boolean includeGenesisRequest(crypto.block.utxo.UTXO request) {
        return true; //can add restrictions later
    }

    private boolean includeUtxoRequest(String id, crypto.block.utxo.UTXO request, Set<String> inputsReferenced) {
        if (!verify(id, request)) {
            return false;
        }

        List<String> transactionInputsToAdd = new ArrayList<>();
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            //check if input has already been used in this same transaction request
            String transactionOutputHash = transactionInput.transactionOutputHash();
            if (transactionInputsToAdd.contains(transactionOutputHash)){
                return false;
                //check if input is already added to the set of transactions for this block;
            } else if (inputsReferenced.contains(transactionOutputHash)){
                return false;
                //check if input is available
            } else if (!Caches.hasUtxo(id, transactionOutputHash)) {
                return false;
            } else {
                transactionInputsToAdd.add(transactionOutputHash);
            }
        }

        return true;
    }

    private static boolean checkInputSumEqualToOutputSum(crypto.block.utxo.UTXO request, String id) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            TransactionOutput transactionOutput = Caches.getUtxo(id, transactionInput.transactionOutputHash());
            long transactionOutputValue = transactionOutput.getValue();
            sumOfInputs += transactionOutputValue;
        }

        for (TransactionOutput transactionOutput : request.getTransactionOutputs()) {
            long transactionOutputValue = transactionOutput.getValue();
            sumOfOutputs += transactionOutputValue;
        }

        return sumOfInputs == sumOfOutputs;
    }

    private boolean isInputSumEqualToOutputSum(String id, crypto.block.utxo.UTXO request) {
        long sum = 0L;
        for (TransactionInput transactionInput : request.getTransactionInputs()) {
            TransactionOutput transactionOutput = Caches.getUtxo(id, transactionInput.transactionOutputHash());
            sum += transactionOutput.getValue();
        }
        for (TransactionOutput transactionOutput : request.getTransactionOutputs()) {
            sum -= transactionOutput.getValue();
        }
        return sum == 0L;
    }

}

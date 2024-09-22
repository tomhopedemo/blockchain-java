package crypto.block;

import crypto.*;
import crypto.caches.UTXOCache;
import crypto.cryptography.ECDSA;
import crypto.encoding.Encoder;
import crypto.hashing.Hashing;
import crypto.signing.Signing;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public record UTXO(List<TransactionInput> transactionInputs,  List<TransactionOutput> transactionOutputs) implements Request<UTXO> {

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
    public void mine(String id, BlockData<UTXO> requests) {
        if (Caches.getChain(id).getMostRecentHash() != null) {
            for (UTXO transactionRequest : requests.data()) {
                if (!verify(id, transactionRequest)) return;
                if (!checkInputSumEqualToOutputSum(transactionRequest, id)) return;
            }
        }

        Set<String> inputs = new HashSet<>();
        for (UTXO utxoRequest : requests.data()) {
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                if (inputs.contains(transactionInput.transactionOutputHash())) return;
                inputs.add(transactionInput.transactionOutputHash());
            }
        }
        addBlock(id, requests);
        Hashing.Type hashType = Caches.getHashType(id);
        for (UTXO utxoRequest : requests.data()) {
            String currency = utxoRequest.getTransactionOutputs().getFirst().currency();
            for (TransactionOutput transactionOutput : utxoRequest.getTransactionOutputs()) {
                Caches.addUtxo(id, currency, transactionOutput.generateTransactionOutputHash(utxoRequest.getBlockDataHash(hashType), hashType), transactionOutput);
            }
            for (TransactionInput transactionInput : utxoRequest.getTransactionInputs()) {
                Caches.removeUtxo(id, currency, transactionInput.transactionOutputHash());
            }
        }
        Requests.remove(id, requests.data(), this.getClass());
    }

    @Override
    public BlockData<UTXO> prepare(String id, List<UTXO> requests) {
        Set<String> inputsReferenced = new HashSet<>();
        List<UTXO> included = new ArrayList<>();
        Blockchain chain = Caches.getChain(id);
        if (chain.getMostRecentHash() == null){
            requests = requests.stream().filter(r -> r.transactionInputs.isEmpty()).toList();
        }

        for (UTXO request : requests) {
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
    public boolean verify(String id, UTXO request) {
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

    private boolean includeGenesisRequest(UTXO request) {
        return true; //can add restrictions later
    }

    private boolean includeUtxoRequest(String id, UTXO request, Set<String> inputsReferenced) {
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

    private static boolean checkInputSumEqualToOutputSum(UTXO request, String id) {
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

    private boolean isInputSumEqualToOutputSum(String id, UTXO request) {
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

    //factory

    public static UTXO genesis(String recipientPublicKey, String currency, long transactionValue) {
        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(recipientPublicKey, currency, transactionValue));
        return new UTXO(new ArrayList<>(), transactionOutputs);
    }

    public static UTXO create(String id, String from, String to, String currency, Long value) throws ChainException {
        Keypair keypair = Caches.getKeypair(id, from);
        if (keypair == null) return null;
        Map<String, TransactionOutput> unspentTransactionOutputsById = getTransactionOutputsById(id, currency, keypair);
        long balance = getBalance(unspentTransactionOutputsById);
        if (balance < value) {
            return null;
        }

        List<TransactionInput> transactionInputs = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, TransactionOutput> entry: unspentTransactionOutputsById.entrySet()){
            String transactionOutputHash = entry.getKey();
            byte[] signature = Signing.sign(keypair, transactionOutputHash);
            transactionInputs.add(new TransactionInput(transactionOutputHash, signature));
            TransactionOutput transactionOutput = entry.getValue();
            total += transactionOutput.getValue();
            if (total >= value) break;
        }

        List<TransactionOutput> transactionOutputs = new ArrayList<>();
        transactionOutputs.add(new TransactionOutput(to, currency, value));
        transactionOutputs.add(new TransactionOutput(keypair.publicKey(), currency, total - value));
        return new UTXO(transactionInputs, transactionOutputs);
    }

    private static long getBalance(Map<String, TransactionOutput> transactionOutputsById) {
        return transactionOutputsById.values().stream()
                .map(transactionOutput -> transactionOutput.getValue()).mapToLong(Long::longValue).sum();
    }

    public static Map<String, TransactionOutput> getTransactionOutputsById(String id, String currency, Keypair keypair) {
        Map<String, TransactionOutput> transactionOutputsById = new HashMap<>();
        UTXOCache utxoCache = Caches.getUTXOCache(id, currency);
        if (utxoCache != null) {
            for (Map.Entry<String, TransactionOutput> item : utxoCache.entrySet()) {
                TransactionOutput transactionOutput = item.getValue();
                if (transactionOutput.getRecipient().equals(keypair.publicKey())) {
                    transactionOutputsById.put(item.getKey(), transactionOutput);
                }
            }
        }
        return transactionOutputsById;
    }

    public record TransactionInput (String transactionOutputHash, String signature) {

        public TransactionInput(String transactionOutputHash, byte[] signature) {
            this(transactionOutputHash, Encoder.encodeToHexadecimal(signature));
        }

        public String serialise(){
            return transactionOutputHash + signature;
        }

    }
}

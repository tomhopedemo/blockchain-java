package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MultiTransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public TransactionCache transactionCache;

    public MultiTransactionBlockMining(Blockchain blockchain, int difficulty, TransactionCache transactionCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.transactionCache = transactionCache;
    }

    public void mineNextBlock(TransactionRequests transactionRequests) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();

        //Verification
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                String transactionOutputHash = transactionInput.getTransactionOutputHash();
                TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
                boolean verified = ECDSA.verifyECDSASignature(Encoder.decodeToPublicKey(transactionOutput.recipient), transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.getSignature()));
                if (!verified){
                    return;
                }
            }

            boolean check = checkInputSumEqualToOutputSum(transactionRequest);
            if (!check){
                return;
            }
        }

        //Create block
        Block block = new Block(transactionRequests, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);

        //Update Caches
        for (TransactionRequest transactionRequest : transactionRequests.getTransactionRequests()) {
            for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
                transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
            }
            for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
                transactionCache.remove(transactionInput.getTransactionOutputHash());
            }
        }
    }

    private boolean checkInputSumEqualToOutputSum(TransactionRequest transactionRequest) {
        long sumOfInputs = 0L;
        long sumOfOutputs = 0L;
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            TransactionOutput transactionOutput = transactionCache.get(transactionInput.getTransactionOutputHash());
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfInputs += transactionOutputValue;
        }

        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            long transactionOutputValue = Long.parseLong(transactionOutput.value);
            sumOfOutputs += transactionOutputValue;
        }

        return sumOfInputs == sumOfOutputs;
    }

}

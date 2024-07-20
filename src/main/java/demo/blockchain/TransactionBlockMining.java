package demo.blockchain;

import demo.cryptography.ECDSA;
import demo.encoding.Encoder;
import demo.objects.Block;
import org.bouncycastle.util.encoders.Hex;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;
    public TransactionCache transactionCache;

    public TransactionBlockMining(Blockchain blockchain, int difficulty, TransactionCache transactionCache) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
        this.transactionCache = transactionCache;
    }

    public void mineNextBlock(TransactionRequest transactionRequest) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();

        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            String transactionOutputHash = transactionInput.getTransactionOutputHash();
            TransactionOutput transactionOutput = transactionCache.get(transactionOutputHash);
            boolean verified = ECDSA.verifyECDSASignature(Encoder.decodeToPublicKey(transactionOutput.recipient), transactionOutputHash.getBytes(UTF_8), Hex.decode(transactionInput.getSignature()));
            if (!verified){
                return;
            }
        }

        for (TransactionOutput transactionOutput : transactionRequest.getTransactionOutputs()) {
            transactionCache.put(transactionOutput.generateTransactionOutputHash(transactionRequest.getTransactionRequestHash()), transactionOutput);
        }
        for (TransactionInput transactionInput : transactionRequest.getTransactionInputs()) {
            transactionCache.remove(transactionInput.getTransactionOutputHash());
        }

        Block block = new Block(transactionRequest, previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);
    }

}

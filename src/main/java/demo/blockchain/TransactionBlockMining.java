package demo.blockchain;

import demo.encoding.Encoder;
import demo.objects.Block;

import java.nio.charset.StandardCharsets;

public class TransactionBlockMining {

    public int difficulty;
    public Blockchain blockchain;

    public TransactionBlockMining(Blockchain blockchain, int difficulty) {
        this.difficulty = difficulty;
        this.blockchain = blockchain;
    }

    public void mineNextBlock(TransactionRequest transactionRequest) throws Exception {
        Block mostRecentBlock = blockchain.getMostRecent();
        String previousHash = mostRecentBlock == null ? null : mostRecentBlock.blockHashId;

        BlockTransactionRequestData blockTransactionRequestData = new BlockTransactionRequestData(Encoder.encode(transactionRequest.senderPublicKeyAddress), Encoder.encode(transactionRequest.recipientPublicKeyAddress), String.valueOf(transactionRequest.transactionValue), String.join(" ", transactionRequest.inputTransactionOutputIds), new String(transactionRequest.signature, StandardCharsets.UTF_8));
        String dataString = blockTransactionRequestData.senderPublicKeyAddressEncoded + blockTransactionRequestData.recipientPublicKeyAddressEncoded
                + blockTransactionRequestData.transactionValue + blockTransactionRequestData.inputTransactionOutputIds + blockTransactionRequestData.transactionRequestSignature;

        Block block = new Block(blockTransactionRequestData, dataString, previousHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineHash(difficulty);
        blockchain.add(block);
    }

    public record BlockTransactionRequestData (
            String senderPublicKeyAddressEncoded,
            String recipientPublicKeyAddressEncoded,
            String transactionValue,
            String inputTransactionOutputIds,
            String transactionRequestSignature){
    }

}

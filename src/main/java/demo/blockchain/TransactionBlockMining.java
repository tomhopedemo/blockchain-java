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
        String previousBlockHash = mostRecentBlock == null ? null : mostRecentBlock.getBlockHashId();

        BlockTransactionRequestData blockTransactionRequestData = new BlockTransactionRequestData(
                Encoder.encode(transactionRequest.senderPublicKeyAddress),
                Encoder.encode(transactionRequest.recipientPublicKeyAddress),
                String.valueOf(transactionRequest.transactionValue),
                String.join(" ", transactionRequest.getTransactionInputs().stream().map(transactionInput -> transactionInput.serialise()).toList()),
                String.join(" ", transactionRequest.getTransactionOutputs().stream().map(transactionOutput -> transactionOutput.serialise()).toList()),
                new String(transactionRequest.transactionHashId, StandardCharsets.UTF_8));


        Block block = new Block(blockTransactionRequestData, blockTransactionRequestData.serialise(), previousBlockHash);
        BlockMiner blockMiner = new BlockMiner(block);
        blockMiner.mineBlockHash("0".repeat(difficulty));
        blockchain.add(block);
    }

    public record BlockTransactionRequestData (
            String senderPublicKeyAddressEncoded,
            String recipientPublicKeyAddressEncoded,
            String transactionValue,
            String inputTransactionDatas,
            String transactionOutputDatas,
            String transactionRequestHash){

        public String serialise(){
            return senderPublicKeyAddressEncoded +
                    recipientPublicKeyAddressEncoded +
                    transactionValue +
                    inputTransactionDatas +
                    transactionOutputDatas +
                    transactionRequestHash;}
    }

}

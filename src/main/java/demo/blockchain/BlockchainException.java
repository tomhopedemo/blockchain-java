package demo.blockchain;

public class BlockchainException extends Exception {

    public BlockchainException(String message) {
        super(message);
    }

    public BlockchainException(Throwable e){
        super(e);
    }
}

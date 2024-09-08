package crypto;

public class ChainException extends Exception {

    public ChainException(String message) {
        super(message);
    }

    public ChainException(Throwable e){
        super(e);
    }
}

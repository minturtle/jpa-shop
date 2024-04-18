package jpabook.jpashop.exception.user.account;

public class TransferFailedException extends Exception{

    public TransferFailedException() {
    }

    public TransferFailedException(String message) {
        super(message);
    }

    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

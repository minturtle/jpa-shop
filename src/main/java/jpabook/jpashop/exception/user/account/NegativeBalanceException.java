package jpabook.jpashop.exception.user.account;

public class NegativeBalanceException extends Exception{

    public NegativeBalanceException() {
    }

    public NegativeBalanceException(String message) {
        super(message);
    }

    public NegativeBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

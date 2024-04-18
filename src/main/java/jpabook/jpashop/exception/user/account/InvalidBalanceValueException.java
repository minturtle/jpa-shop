package jpabook.jpashop.exception.user.account;

public class InvalidBalanceValueException extends Exception{

    public InvalidBalanceValueException() {
    }

    public InvalidBalanceValueException(String message) {
        super(message);
    }

    public InvalidBalanceValueException(String message, Throwable cause) {
        super(message, cause);
    }
}

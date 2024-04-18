package jpabook.jpashop.exception.user.account;

public class UnauthorizedAccountAccessException extends Exception{

    public UnauthorizedAccountAccessException() {
    }

    public UnauthorizedAccountAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccountAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

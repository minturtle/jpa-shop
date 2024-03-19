package jpabook.jpashop.exception.user.account;

public class CannotFindAccountException extends Exception{

    public CannotFindAccountException() {
    }

    public CannotFindAccountException(String message) {
        super(message);
    }

    public CannotFindAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}

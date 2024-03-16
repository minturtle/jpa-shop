package jpabook.jpashop.exception.user;

public class CannotFindUserException extends Exception{
    public CannotFindUserException() {
    }

    public CannotFindUserException(String message) {
        super(message);
    }

    public CannotFindUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

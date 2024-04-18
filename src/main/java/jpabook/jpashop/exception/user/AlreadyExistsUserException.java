package jpabook.jpashop.exception.user;

public class AlreadyExistsUserException extends Exception{

    public AlreadyExistsUserException() {
    }

    public AlreadyExistsUserException(String message) {
        super(message);
    }

    public AlreadyExistsUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

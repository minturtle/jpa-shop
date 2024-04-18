package jpabook.jpashop.exception.user;

public class UserAuthTypeException extends Exception{

    public UserAuthTypeException() {
    }

    public UserAuthTypeException(String message) {
        super(message);
    }

    public UserAuthTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

package jpabook.jpashop.exception.user;

public class UserUpdateFailureException extends Exception{

    public UserUpdateFailureException() {
    }

    public UserUpdateFailureException(String message) {
        super(message);
    }

    public UserUpdateFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}

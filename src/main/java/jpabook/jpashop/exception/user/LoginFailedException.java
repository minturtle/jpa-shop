package jpabook.jpashop.exception.user;

public class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailedException() {
    }
}

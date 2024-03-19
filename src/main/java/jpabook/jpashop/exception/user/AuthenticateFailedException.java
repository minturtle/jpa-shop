package jpabook.jpashop.exception.user;

public class AuthenticateFailedException extends Exception {
    public AuthenticateFailedException(String message) {
        super(message);
    }

    public AuthenticateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticateFailedException() {
    }
}

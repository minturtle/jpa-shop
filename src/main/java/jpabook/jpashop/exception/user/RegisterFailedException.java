package jpabook.jpashop.exception.user;

public class RegisterFailedException extends RuntimeException {
    public RegisterFailedException() {
    }

    public RegisterFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterFailedException(String message) {
        super(message);
    }
}

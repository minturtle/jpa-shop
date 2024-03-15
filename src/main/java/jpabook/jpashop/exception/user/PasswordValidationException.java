package jpabook.jpashop.exception.user;

public class PasswordValidationException extends Exception{
    public PasswordValidationException() {
    }

    public PasswordValidationException(String message) {
        super(message);
    }

    public PasswordValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

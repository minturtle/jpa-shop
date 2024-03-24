package jpabook.jpashop.exception.common;

public class CannotFindEntityException extends Exception{

    public CannotFindEntityException() {
    }

    public CannotFindEntityException(String message) {
        super(message);
    }

    public CannotFindEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}

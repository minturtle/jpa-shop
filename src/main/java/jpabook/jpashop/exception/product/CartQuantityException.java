package jpabook.jpashop.exception.product;

public class CartQuantityException extends Exception{

    public CartQuantityException() {
    }

    public CartQuantityException(String message) {
        super(message);
    }

    public CartQuantityException(String message, Throwable cause) {
        super(message, cause);
    }
}


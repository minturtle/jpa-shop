package jpabook.jpashop.exception.product;

public class InvalidStockQuantityException extends Exception{

    public InvalidStockQuantityException() {
    }

    public InvalidStockQuantityException(String message) {
        super(message);
    }

    public InvalidStockQuantityException(String message, Throwable cause) {
        super(message, cause);
    }
}

package jpabook.jpashop.controller.order;

import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrderControllerAdvice {

    @ExceptionHandler({InvalidBalanceValueException.class, InvalidStockQuantityException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }

}

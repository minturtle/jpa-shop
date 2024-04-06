package jpabook.jpashop.controller.product;


import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.exception.product.CartQuantityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ProductControllerAdvice {

    @ExceptionHandler(CartQuantityException.class)
    public ResponseEntity<ErrorResponse> badRequest(Exception e){
        log.info("method execution failed-400 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }

}

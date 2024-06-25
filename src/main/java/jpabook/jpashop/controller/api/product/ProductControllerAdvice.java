package jpabook.jpashop.controller.api.product;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jpabook.jpashop.controller.api.common.response.ErrorResponse;
import jpabook.jpashop.exception.product.CartQuantityException;
import jpabook.jpashop.exception.product.InvalidStockQuantityException;
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


    /**
     * @author minseok kim
     * @description 상품 주문, 카트 추가 및 수정에 대해 수량 추가가 불가능할 시 나타나는 오류
     * @param e Exception 객체
     * @return 400 Http Code With ErrorResponse Object
     */

    @ExceptionHandler({CartQuantityException.class, InvalidStockQuantityException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "400")
    })
    public ResponseEntity<ErrorResponse> badRequest(Exception e){
        log.info("method execution failed-400 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }

}

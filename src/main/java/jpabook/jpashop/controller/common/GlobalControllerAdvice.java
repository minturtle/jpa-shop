package jpabook.jpashop.controller.common;

import io.jsonwebtoken.ExpiredJwtException;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {


    @ExceptionHandler({CannotFindEntityException.class})
    public ResponseEntity<ErrorResponse> notfound(Exception e){
        log.info("method execution failed-404 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler({OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> conflict(Exception e){
        log.info("method execution failed-409 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.CONFLICT);
    }
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> unAuthorized(Exception e){
        log.info("method execution failed-401 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({InternalErrorException.class, Exception.class})
    public ResponseEntity<ErrorResponse> unexpectedException(Exception e){
        log.warn("method execution failed-500 : {} - unexpected exception", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

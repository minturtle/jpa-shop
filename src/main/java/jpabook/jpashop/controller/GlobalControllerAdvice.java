package jpabook.jpashop.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.exception.common.InternalErrorException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler({OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> conflict(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.CONFLICT);
    }
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> unAuthorized(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({InternalErrorException.class, Exception.class})
    public ResponseEntity<ErrorResponse> unexpectedException(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

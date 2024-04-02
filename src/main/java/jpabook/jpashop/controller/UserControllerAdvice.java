package jpabook.jpashop.controller;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.AuthenticateFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserAuthTypeException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;


@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler({ PasswordValidationException.class, AlreadyExistsUserException.class, JwtException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({ExpiredJwtException.class, AuthenticateFailedException.class})
    public ResponseEntity<ErrorResponse> unAuthorized(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserAuthTypeException.class, UnauthorizedAccountAccessException.class})
    public ResponseEntity<ErrorResponse> forbidden(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> conflict(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.CONFLICT);
    }


}


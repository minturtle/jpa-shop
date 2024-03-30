package jpabook.jpashop.controller;


import jakarta.persistence.EntityNotFoundException;
import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.exception.user.PasswordValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;


@RestControllerAdvice
public class UserControllerAdvice {




    @ExceptionHandler({ PasswordValidationException.class })
    public ResponseEntity<ErrorResponse> invalidPassword(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }


}


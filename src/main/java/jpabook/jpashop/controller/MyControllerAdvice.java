package jpabook.jpashop.controller;


import jakarta.persistence.EntityNotFoundException;
import jpabook.jpashop.controller.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class MyControllerAdvice {


    /*
    * 아래의 경우에 메서드가 실행됨.
    *
    * 사용자가 검색을 할 때 잘못된 값으로 검색을 할 때
    * 사용자가 유효하지 않은, 잘못된 값을 입력할 때
    * 세션에 잘못된 userId가 들어가 있을 때
    * */
    @ExceptionHandler({ EntityNotFoundException.class, IllegalStateException.class })
    public ResponseEntity<ErrorResponse> entityNotFound(Exception e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }


}


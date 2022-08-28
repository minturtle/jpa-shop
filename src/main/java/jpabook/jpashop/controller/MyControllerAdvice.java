package jpabook.jpashop.controller;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class MyControllerAdvice {


    /*
    * 사용자가 검색을 할 때 잘못된 값으로 검색을 한다면 실행되는 메서드
    *
    * */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity entityNotFound(Exception e){

        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
class ErrorResponse{
    private String message;
}

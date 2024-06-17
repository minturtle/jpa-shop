package jpabook.jpashop.controller.user;


import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jpabook.jpashop.controller.common.response.ErrorResponse;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.AuthenticateFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserAuthTypeException;
import jpabook.jpashop.exception.user.account.InvalidBalanceValueException;
import jpabook.jpashop.exception.user.account.UnauthorizedAccountAccessException;
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
public class UserControllerAdvice {

    @ExceptionHandler({ PasswordValidationException.class, AlreadyExistsUserException.class, JwtException.class, InvalidBalanceValueException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "400")
    })
    public ResponseEntity<ErrorResponse> badRequest(Exception e){
        log.info("method execution failed-400 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({AuthenticateFailedException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "401")
    })
    public ResponseEntity<ErrorResponse> unAuthorized(Exception e){
        log.info("method execution failed-401 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserAuthTypeException.class, UnauthorizedAccountAccessException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "403")
    })
    public ResponseEntity<ErrorResponse> forbidden(Exception e){
        log.info("method execution failed-403 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.FORBIDDEN);
    }


}


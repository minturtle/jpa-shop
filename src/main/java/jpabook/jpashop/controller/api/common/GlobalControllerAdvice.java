package jpabook.jpashop.controller.api.common;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jpabook.jpashop.controller.api.common.response.ErrorResponse;
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


    /**
     * @author minseok kim
     * @description Entity의 ID(UID)를 입력받았으나, Entity를 조회할 수 없는 경우 실행되는 메서드
     * @param e Exception 객체
     * @return 404 Http Code With ErrorResponse Object
    */
    @ExceptionHandler({CannotFindEntityException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "404")
    })
    public ResponseEntity<ErrorResponse> notfound(Exception e){
        log.info("method execution failed-404 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.NOT_FOUND);
    }



    /**
     * @author minseok kim
     * @description 낙관적 락 충돌, 즉 동시성 버전 오류 발생시 실행되는 메서드
     * @param e Exception 객체
     * @return 409 Http Code With ErrorResponse Object
    */
    @ExceptionHandler({OptimisticLockingFailureException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "409")
    })
    public ResponseEntity<ErrorResponse> conflict(Exception e){
        log.info("method execution failed-409 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.CONFLICT);
    }

    /**
     * @author minseok kim
     * @description JWT Token 만료시 나타나는 메서드
     * @param e Exception 객체
     * @return 401 Http Code With ErrorResponse Object
    */
    @ExceptionHandler({ExpiredJwtException.class})
    @ApiResponses({
            @ApiResponse(responseCode = "401")
    })
    public ResponseEntity<ErrorResponse> unAuthorized(Exception e){
        log.info("method execution failed-401 : {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.UNAUTHORIZED);
    }


    /**
     * @author minseok kim
     * @description 기타 서버에서 발생한 예상치못한 Exception
     * @param e Exception 객체
     * @return 500 Http Code With ErrorResponse Object
    */
    @ExceptionHandler({InternalErrorException.class, Exception.class})
    @ApiResponses({
            @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<ErrorResponse> unexpectedException(Exception e){
        log.warn("method execution failed-500 : {} - unexpected exception", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), Arrays.toString(e.getStackTrace())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

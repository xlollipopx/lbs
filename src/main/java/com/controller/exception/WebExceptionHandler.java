package com.controller.exception;

import com.controller.HomeController;
import com.dto.ErrorDto;
import com.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice(assignableTypes = {
        HomeController.class
})
public class WebExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDto<ErrorDto>> unauthorized(Exception e) {
        log.warn(e.getMessage());
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<ErrorDto>> internalServerError(Exception e) {
        log.error(e.getMessage(), e);
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<ResponseDto<T>> buildResponse(T data, HttpStatus httpStatus) {
        ResponseDto<T> response = new ResponseDto<>(data);
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<ResponseDto<T>> buildErrorResponse(
            String errorMsg, HttpStatus httpStatus) {
        ResponseEntity<ResponseDto<T>> response = buildResponse(null, httpStatus);
        response.getBody().setError(new ErrorDto.Error(errorMsg));
        return response;
    }
}

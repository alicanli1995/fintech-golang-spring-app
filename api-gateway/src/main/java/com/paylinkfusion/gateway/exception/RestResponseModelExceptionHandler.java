package com.paylinkfusion.gateway.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseModelExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("400")
                .errorMessage(e.getMessage().split(":")[1].trim())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("400")
                .errorMessage(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(NullPointerException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("400")
                .errorMessage(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> handleRequestNotPermitted(RequestNotPermitted e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("429")
                .errorMessage(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("400")
                .errorMessage(fieldError != null ? fieldError.getDefaultMessage() : null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

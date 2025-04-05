package com.microservice.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String , String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().stream()
                        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
       ErrorResponse errorResponse = new ErrorResponse().builder().result(errors).status("fail").message("Loi valid")
        .build();

    return   ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);


    }
}
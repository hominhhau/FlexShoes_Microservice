package iuh.fit.se.profileservice.exceptions;

import iuh.fit.se.profileservice.dtos.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity
                .badRequest()
                .body(
                        ApiResponse.builder()
                                .status("FAILED")
                                .message("Registration Failed: Please provide valid data.")
                                .response(errors)
                                .build()
                );
    }

    @ExceptionHandler(value = ProfileAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> userAlreadyExistsExceptionHandler(ProfileAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ApiResponse.builder()
                                .status("FAILED")
                                .message(exception.getLocalizedMessage())
                                .build()
                );
    }
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> userFailtoCreateExceptionHandler(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        ApiResponse.builder()
                                .status("FAILED")
                                .message(exception.getMessage())
                                .build()
                );
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> globalExceptionHandler(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.builder()
                                .status("FAILED")
                                .message(exception.getMessage())
                                .build()
                );
    }

}

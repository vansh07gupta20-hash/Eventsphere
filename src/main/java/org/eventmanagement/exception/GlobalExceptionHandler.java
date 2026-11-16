package org.eventmanagement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException exception){

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        Map<String , String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();

            errors.put(fieldName , message);
        }
        return ResponseEntity.badRequest().body(errors);
    }
}

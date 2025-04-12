package com.example.walletrest.presentation.exception.handler;

import com.example.walletrest.application.exception.InvalidAssetPriceException;
import com.example.walletrest.application.exception.ResourceNotFoundException;
import com.example.walletrest.application.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExist(UserAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, ex.getMessage()));
    }
    @ExceptionHandler(InvalidAssetPriceException.class)
    public ResponseEntity<ApiError> handleInvalidAssetPrice(InvalidAssetPriceException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(400, ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField().toUpperCase(Locale.ROOT))
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; "));
        if (!errorMessage.isEmpty()) {
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length());
        } else {
            errorMessage.append("Validation failed");
        }
        log.error(ex.getMessage());
        ApiError apiError = ApiError.of(HttpStatus.BAD_REQUEST.value(), errorMessage.toString());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
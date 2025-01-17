// File: main/java/com/thegoalgrid/goalgrid/exception/GlobalExceptionHandler.java
package com.thegoalgrid.goalgrid.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, String>> handleClassCastException(ClassCastException ex){
        logger.error("ClassCastException: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error. Please contact support.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex){
        logger.error("RuntimeException: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex){
        logger.error("Exception: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Add more specific exception handlers as needed
}

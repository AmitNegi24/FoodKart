package com.foodkart.menu_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(
            RestaurantNotFoundException ex,
            HttpServletRequest request) {
        log.error("RestaurantId not found: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ErrorResponse> handleEnumBindingError(
//            MethodArgumentTypeMismatchException ex,
//            HttpServletRequest request) {
//
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setTimestamp(LocalDateTime.now());
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setError("Bad Request");
//
//        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
//            String validValues = Arrays.toString(ex.getRequiredType().getEnumConstants());
//            errorResponse.setMessage("Invalid value '" + ex.getValue() +
//                    "'. Valid values are: " + validValues);
//        } else {
//            errorResponse.setMessage("Invalid request parameter");
//        }
//
//        errorResponse.setPath(request.getRequestURI());
//
//        return ResponseEntity.badRequest().body(errorResponse);
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEnum(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {

            String invalidValue =
                    invalidFormatException.getValue().toString();

            Class<?> enumClass =
                    invalidFormatException.getTargetType();

            String validValues =
                    Arrays.toString(enumClass.getEnumConstants());

            String message =
                    "Invalid value '" + invalidValue +
                            "'. Allowed values are: " + validValues;

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", message));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Invalid Enum Value"));
    }

    //MenuItemNotFound exception handler by Amit Negi
    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMenuItemNotFoundException(
            MenuItemNotFoundException ex,
            HttpServletRequest request) {
        log.error("MenuItem not found: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
}



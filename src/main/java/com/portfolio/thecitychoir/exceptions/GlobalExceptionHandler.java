package com.portfolio.thecitychoir.exceptions;

import com.portfolio.thecitychoir.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailExists(
            EmailAlreadyRegisteredException ex,
            HttpServletRequest request
    ) {
        return buildError(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> details = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        return buildError(
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                request,
                details.toString()
        );
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJwt(
            InvalidJwtException ex,
            HttpServletRequest request
    ) {
        return buildError(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                "You do not have permission to perform this action",
                HttpStatus.FORBIDDEN,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOtherExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        return buildError(message, status, request, null);
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            String message,
            HttpStatus status,
            HttpServletRequest request,
            String details
    ) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(details == null ? message : message + " | " + details)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}

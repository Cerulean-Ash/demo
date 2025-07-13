package com.example.demo.controlleradvice;

import com.example.demo.dto.errors.BadRequestErrorResponseDto;
import com.example.demo.dto.errors.ErrorDetailDto;
import com.example.demo.dto.errors.ErrorResponseDto;
import com.example.demo.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        logger.error("ResourceNotFoundException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException exception, WebRequest request) {
        logger.error("UserAlreadyExistsException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserHasAccountException.class)
    public ResponseEntity<ErrorResponseDto> handleUserHasAccountsException(UserHasAccountException exception, WebRequest request) {
        logger.error("UserHasAccountsException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFoundException(AccountNotFoundException exception, WebRequest request) {
        logger.error("AccountNotFoundException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTransactionNotFoundException(TransactionNotFoundException exception, WebRequest request) {
        logger.error("TransactionNotFoundException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientFundsException(InsufficientFundsException exception, WebRequest request) {
        logger.error("InsufficientFundsException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccountOwnershipException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountOwnershipException(AccountOwnershipException exception, WebRequest request) {
        logger.error("AccountOwnershipException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BadRequestErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        logger.error("IllegalArgumentException: {}", exception.getMessage(), exception);
        List<ErrorDetailDto> details = List.of(new ErrorDetailDto("request", exception.getMessage(), "validation"));
        BadRequestErrorResponseDto errorResponse = new BadRequestErrorResponseDto("Invalid details supplied", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        logger.error("AccessDeniedException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException exception, WebRequest request) {
        logger.error("MethodArgumentNotValidException: {}", exception.getMessage(), exception);
        List<ErrorDetailDto> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetailDto(
                        error.getField(),
                        Optional.ofNullable(error.getDefaultMessage()).orElse("Validation error"),
                        "validation")
                )
                .collect(Collectors.toList());

        BadRequestErrorResponseDto errorResponse = new BadRequestErrorResponseDto("Invalid details supplied", details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest request) {
        logger.error("An unexpected error occurred: {}", exception.getMessage(), exception);
        ErrorResponseDto errorResponse = new ErrorResponseDto("An unexpected error occurred");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountHasNonZeroBalanceException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountHasNonZeroBalanceException(AccountHasNonZeroBalanceException exception, WebRequest request) {
        logger.error("AccountHasNonZeroBalanceException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorDetails = new ErrorResponseDto(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request) {
        logger.error("DataIntegrityViolationException: {}", exception.getMessage(), exception);
        ErrorResponseDto errorDetails = new ErrorResponseDto("Data integrity violation: " + exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
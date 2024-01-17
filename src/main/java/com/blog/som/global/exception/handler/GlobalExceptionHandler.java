package com.blog.som.global.exception.handler;

import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.ErrorResponse;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.CustomSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
        e.getMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getErrorCode().getStatus());
  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
  }

  @ExceptionHandler(CustomSecurityException.class)
  public ResponseEntity<ErrorResponse> handleSecurityException(CustomSecurityException e) {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
  }

}

package com.mihirsoni.radical.bookworm.controller;

import com.mihirsoni.radical.bookworm.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BookwormControllerAdvice {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleException(Exception e) {
    ErrorDto errorDto = new ErrorDto(e.getMessage());
    errorDto.setCode("500");
    return ResponseEntity.status(500).body(errorDto);
  }
}

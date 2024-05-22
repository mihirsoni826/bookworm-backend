package com.mihirsoni.radical.bookworm.dto;

import lombok.Data;

@Data
public class ErrorDto {
  private String message;
  private String code;

  public ErrorDto(String message) {
    this.message = message;
  }
}

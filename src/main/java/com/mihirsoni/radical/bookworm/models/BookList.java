package com.mihirsoni.radical.bookworm.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Embeddable
@Data
@AllArgsConstructor
public class BookList {
  private String displayName;
  private String encodedName;
}

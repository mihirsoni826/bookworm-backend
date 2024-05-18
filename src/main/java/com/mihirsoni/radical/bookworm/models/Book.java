package com.mihirsoni.radical.bookworm.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "books")
@Builder
@Data
public class Book {
  @Id private String isbn;
  private String title;
  private String author;
  private int rating;
  private String imageUrl;
  private boolean isFavourite;
  private double price;
  private String listName;
  private String encodedListName;
}

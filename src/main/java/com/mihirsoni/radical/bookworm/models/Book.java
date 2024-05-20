package com.mihirsoni.radical.bookworm.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
  @Id private String isbn;
  private String title;
  private String author;
  private int rating;
  private String imageUrl;
  private boolean isFavourite;
  private int price;
  private String listName;
  private String encodedListName;

  /**
   * checks if two objects are equal based on their ISBN
   *
   * @param o object to be compared
   * @return true if objects are equal else false
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return isbn != null ? isbn.equals(book.isbn) : book.isbn == null;
  }

  /**
   * calculates hashcode based on ISBN
   *
   * @return hashcode
   */
  @Override
  public int hashCode() {
    return isbn != null ? isbn.hashCode() : 0;
  }
}

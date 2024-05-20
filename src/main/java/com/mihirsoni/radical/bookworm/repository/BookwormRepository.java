package com.mihirsoni.radical.bookworm.repository;

import com.mihirsoni.radical.bookworm.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookwormRepository extends JpaRepository<Book, String> {
  @Query("SELECT b FROM Book b WHERE b.isFavourite = true")
  List<Book> findByFavourite();
}

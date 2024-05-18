package com.mihirsoni.radical.bookworm.repository;

import com.mihirsoni.radical.bookworm.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookwormRepository extends JpaRepository<Book, String> {}

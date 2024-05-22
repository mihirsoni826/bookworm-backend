package com.mihirsoni.radical.bookworm.repository;

import static com.mihirsoni.radical.bookworm.utils.TestUtils.createBook;
import static org.junit.jupiter.api.Assertions.*;

import com.mihirsoni.radical.bookworm.models.Book;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BookwormRepositoryTest {

  @Autowired BookwormRepository repository;

  @Test
  void BookwormRepository_FindByValidId_ReturnsBook() {
    String isbn = "9781541673526";

    repository.save(Book.builder().isbn(isbn).build());
    Book book = repository.findById(isbn).orElse(null);

    assertNotNull(book);
    assertEquals(isbn, book.getIsbn());
  }

  @Test
  void BookwormRepository_FindByInvalidId_ReturnsNull() {
    String isbn = "1234567890123";

    Book book = repository.findById(isbn).orElse(null);

    assertNull(book);
  }

  @Test
  void BookwormRepository_SaveBook_SavesBook() {
    Book book = createBook(3, 10, true, true);

    repository.save(book);
    Book savedBook = repository.findById(book.getIsbn()).orElse(null);

    assertNotNull(savedBook);
    assertEquals(book.getIsbn(), savedBook.getIsbn());
  }

  @Test
  void BookwormRepository_RemoveBook_RemovesBook() {
    Book book = createBook(3, 10, true, true);

    repository.save(book);
    repository.delete(book);
    Book deletedBook = repository.findById(book.getIsbn()).orElse(null);

    assertNull(deletedBook);
  }

  @Test
  void BookwormRepository_FindByFavourite_ReturnsListOfFavourites() {
    Book book1 = createBook(3, 10, true, true);
    Book book2 = createBook(1, 24, true, true);
    book2.setIsbn("456");

    repository.save(book1);
    repository.save(book2);
    List<Book> favourites = repository.findByFavourite();

    assertNotNull(favourites);
    assertEquals(2, favourites.size());
  }

  @Test
  void BookwormRepository_UpdateBook_UpdatesBook() {
    Book book = createBook(3, 10, true, true);

    repository.save(book);
    Book savedBook = repository.findById(book.getIsbn()).orElse(null);

    assertNotNull(savedBook);
    savedBook.setRating(4);
    savedBook.setPrice(1000);
    savedBook.setFavourite(false);

    repository.save(savedBook);

    Book updatedBook = repository.findById(book.getIsbn()).orElse(null);

    assertNotNull(updatedBook);
    assertEquals(4, updatedBook.getRating());
    assertEquals(1000, updatedBook.getPrice());
    assertFalse(updatedBook.isFavourite());
  }
}

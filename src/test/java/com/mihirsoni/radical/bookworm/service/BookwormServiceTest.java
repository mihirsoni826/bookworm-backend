package com.mihirsoni.radical.bookworm.service;

import static com.mihirsoni.radical.bookworm.utils.TestUtils.createBook;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihirsoni.radical.bookworm.config.BookwormConfiguration;
import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.repository.BookwormRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class BookwormServiceTest {

  @Mock private BookwormRepository repository;
  @Mock private RestTemplate restTemplate;
  @Mock private BookwormConfiguration configuration;
  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks private BookwormService service;

  /**
   * Checks if bestsellers are returned correctly if present
   *
   * @throws IOException if file not found or response is "un-parse-able"
   */
  @Test
  void BookwormService_FetchBestsellers_ReturnsSetOfBestsellers() throws IOException {
    when(restTemplate.getForObject(
            "https://api.nytimes.com/svc/books/v3/lists/full-overview.json?api-key=DUMMY_API_KEY",
            String.class))
        .thenReturn(
            new String(Files.readAllBytes(Paths.get("src/test/resources/external_response.json"))));
    when(repository.existsById(anyString())).thenReturn(false);
    when(configuration.getApiKey()).thenReturn("DUMMY_API_KEY");

    Set<Book> books = service.fetchAllBestsellersAcrossCategoriesFromNYTAPI();
    assertNotNull(books);
    assertFalse(books.isEmpty());
  }

  /** Checks if favourites are returned correctly if present */
  @Test
  void BookwormService_FetchFavourites_ReturnListOfFavourites() {
    Book book1 = createBook(3, 10, true, true);
    Book book2 = createBook(1, 24, true, true);
    book2.setIsbn("456");
    List<Book> list = List.of(book1, book2);

    when(repository.findByFavourite()).thenReturn(list);

    List<Book> favourites = service.getFavouriteList();

    assertNotNull(favourites);
    assertEquals(2, favourites.size());
  }

  /** Checks if empty list is returned when no favourites are present */
  @Test
  void BookwormService_FetchFavourites_ReturnsEmptyList1() {
    when(repository.findByFavourite()).thenReturn(List.of());

    List<Book> favourites = service.getFavouriteList();

    assertNotNull(favourites);
    assertTrue(favourites.isEmpty());
  }

  /** Checks if deleteAll() is called when purgeDatabase() is called */
  @Test
  void BookwormService_PurgeDatabase_PurgesDatabase() {
    doNothing().when(repository).deleteAll();

    service.purgeDatabase();

    Mockito.verify(repository, times(1)).deleteAll();
  }

  /** Checks if updateRatingAndPrice() updates already existing book in the repository */
  @Test
  void BookwormService_UpdateRatingPrice_SavesUpdatedBook() {
    Book book = mock(Book.class);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.of(book));
    when(repository.save(book)).thenReturn(book);

    service.updateRatingAndPrice(book);

    Mockito.verify(repository, times(1)).save(book);
    Mockito.verify(book, times(1)).setRatingPriceChanged(true);
    Mockito.verify(book, times(2)).setRating(book.getRating());
    Mockito.verify(book, times(2)).setPrice(book.getPrice());
  }

  /** Checks if updateRatingAndPrice() saves new book in the repository */
  @Test
  void BookwormService_UpdateRatingPrice_SavesNewBook() {
    Book book = mock(Book.class);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.empty());
    when(repository.save(book)).thenReturn(book);

    service.updateRatingAndPrice(book);

    Mockito.verify(repository, times(1)).save(book);
    Mockito.verify(book, times(1)).setRatingPriceChanged(true);
    Mockito.verify(book, times(1)).setRating(book.getRating());
    Mockito.verify(book, times(1)).setPrice(book.getPrice());
  }

  /**
   * Checks if removeFromFavourite() removes book from database if rating and price never changed
   */
  @Test
  void BookwormService_RemoveFavourite_RemovesFromDatabaseIfRatingPriceNeverChanged() {
    Book book = createBook(3, 10, true, false);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.of(book));

    service.removeFromFavourite(book);

    Mockito.verify(repository, times(1)).delete(book);
    Mockito.verify(repository, times(0)).save(book);
  }

  /** Checks if removeFromFavourite() updates and save book if rating and price has changed */
  @Test
  void BookwormService_RemoveFavourite_RemovesFavouriteIfRatingPriceChanged() {
    Book book = createBook(3, 10, true, true);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.of(book));

    service.removeFromFavourite(book);

    Mockito.verify(repository, times(0)).delete(book);
    Mockito.verify(repository, times(1)).save(book);
  }

  /** Checks if removeFromFavourite() does nothing if book does not exist in the database */
  @Test
  void BookwormService_RemoveFavourite_DoesNothingIfBookDoesNotExist() {
    when(repository.findById(anyString())).thenReturn(Optional.empty());

    service.removeFromFavourite(createBook(3, 10, true, true));

    Mockito.verify(repository, times(0)).delete(any());
    Mockito.verify(repository, times(0)).save(any());
  }

  /** Checks if addToFavourite() updates and save book if book already exists in the database */
  @Test
  void BookwormService_AddFavourite_UpdatesExistingBook() {
    Book book = createBook(3, 10, false, true);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.of(book));

    service.addToFavourite(book);

    Mockito.verify(repository, times(1)).save(book);
  }

  /** Checks if addToFavourite() saves new book if book does not exist in the database */
  @Test
  void BookwormService_AddFavourite_SavesNewBook() {
    Book book = createBook(3, 10, false, false);
    when(repository.findById(book.getIsbn())).thenReturn(java.util.Optional.empty());

    service.addToFavourite(book);

    Mockito.verify(repository, times(1)).save(book);
  }
}

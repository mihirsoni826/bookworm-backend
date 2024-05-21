package com.mihirsoni.radical.bookworm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihirsoni.radical.bookworm.config.BookwormConfiguration;
import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.repository.BookwormRepository;
import com.mihirsoni.radical.bookworm.utils.Constants;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@AllArgsConstructor
@Slf4j
public class BookwormService {
  private final BookwormRepository repository;
  private final RestTemplate restTemplate;
  private final BookwormConfiguration configuration;
  private final ObjectMapper objectMapper;

  /**
   * Fetches all bestsellers across all categories from New York Times API (full-overview)
   *
   * @return Set of bestseller books
   */
  public Set<Book> fetchAllBestsellersAcrossCategoriesFromNYTAPI() {
    String uri = buildUri();
    String jsonResponse = restTemplate.getForObject(uri, String.class);

    Set<Book> books = new HashSet<>();
    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);
      JsonNode listsNode = rootNode.path("results").path("lists");

      if (listsNode.isArray()) {
        for (JsonNode listNode : listsNode) {
          JsonNode booksNode = listNode.path("books");
          if (booksNode.isArray()) {
            for (JsonNode bookNode : booksNode) {
              String isbn = bookNode.path("primary_isbn13").asText();
              if (repository.existsById(isbn)) {
                Optional<Book> book = repository.findById(isbn);
                book.ifPresent(books::add);
                continue;
              }
              Book book = buildBookObjectFromJsonNode(bookNode, listNode, isbn);
              books.add(book);
            }
          }
        }
      }
    } catch (IOException e) {
      log.error("Error while fetching data from New York Times API", e);
    }
    return books;
  }

  /**
   * Builds book object from json node and list node Generates random price and rating, 5-15 and 1-5
   * respectively
   *
   * @param bookNode json node of book
   * @param listNode json node of list
   * @param isbn ISBN of book
   * @return Book object
   */
  private Book buildBookObjectFromJsonNode(JsonNode bookNode, JsonNode listNode, String isbn) {
    return Book.builder()
        .title(bookNode.path("title").asText())
        .author(bookNode.path("author").asText())
        .imageUrl(bookNode.path("book_image").asText())
        .isFavourite(false)
        .price(
            bookNode.path("price").asText().equals("0.00")
                ? generateRandomNumber()
                : Integer.parseInt(bookNode.path("price").asText()))
        .listName(listNode.path("list_name").asText())
        .encodedListName(listNode.path("list_name_encoded").asText())
        .rating(generateRandomRating())
        .isbn(isbn)
        .build();
  }

  /**
   * Builds URI for external API call with query params.<br>
   * Built with the idea that this could be used for multiple API endpoints. <b>Hardcoded URL for
   * now to prevent code smells</b>
   *
   * @return Built URI in String format
   */
  private String buildUri() {
    return UriComponentsBuilder.fromUriString(Constants.GET_FULL_OVERVIEW)
        .queryParam("api-key", configuration.getApiKey())
        .toUriString();
  }

  /**
   * Adds a book to favourite list if it does not already exist. If it does, just marks it as
   * favourite
   *
   * @param book Book object to be marked as favourite
   */
  public void addToFavourite(Book book) {
    repository
        .findById(book.getIsbn())
        .ifPresentOrElse(
            (oldBook) -> {
              oldBook.setFavourite(true);
              repository.save(oldBook);
              log.info(
                  "Book with ISBN {} already exists in the favourite list, marked as favourite",
                  book.getIsbn());
            },
            () -> {
              book.setFavourite(true); // ensure that book is marked as favourite
              repository.save(book);
              log.info("Book with ISBN {} added to favourite list", book.getIsbn());
            });
  }

  /**
   * Removes a book from favourite list if it already exists
   *
   * @param book Book object to be removed from favourite list
   */
  public void removeFromFavourite(Book book) {
    repository
        .findById(book.getIsbn())
        .ifPresentOrElse(
            (oldBook) -> {
              oldBook.setFavourite(false);
              repository.save(oldBook);
              log.info("Book with ISBN {} removed from favourite list", book.getIsbn());
            },
            () ->
                log.info(
                    "Cannot remove book with ISBN {} as it does not exist in the favourite list",
                    book.getIsbn()));
  }

  /**
   * Updates rating and/or price of a book if it already exists
   *
   * @param book Book object with the new values
   */
  public void updateRatingAndPrice(Book book) {
    repository
        .findById(book.getIsbn())
        .ifPresentOrElse(
            (oldBook) -> {
              oldBook.setRating(book.getRating());
              oldBook.setPrice(book.getPrice());
              repository.save(oldBook);
            },
            () -> repository.save(book));
    log.info(
        "Book with ISBN {} updated with rating {} and price {}",
        book.getIsbn(),
        book.getRating(),
        book.getPrice());
  }

  /**
   * Fetches all books from favourite list
   *
   * @return List of favourite books
   */
  public List<Book> getFavouriteList() {
    List<Book> list = repository.findByFavourite();
    log.info("{} favourite bestsellers fetched", list.size());
    return list;
  }

  /** Purges database and resets application */
  public void purgeDatabase() {
    repository.deleteAll();
    log.info("Database purged");
  }

  /**
   * Generates random rating between 1 and 5
   *
   * @return Random rating
   */
  private int generateRandomRating() {
    return (int) (Math.random() * 5) + 1;
  }

  /**
   * Generates random number between 5 and 20
   *
   * @return Random number
   */
  private int generateRandomNumber() {
    return (int) (Math.random() * 15) + 5;
  }
}

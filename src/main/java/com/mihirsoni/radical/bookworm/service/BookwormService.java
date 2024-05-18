package com.mihirsoni.radical.bookworm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihirsoni.radical.bookworm.config.BookwormConfiguration;
import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.repository.BookwormRepository;
import com.mihirsoni.radical.bookworm.utils.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  public List<Book> fetchAllBestsellersAcrossCategoriesFromNYT() {
    String uri = buildUri(Constants.GET_FULL_OVERVIEW);
    String jsonResponse = restTemplate.getForObject(uri, String.class);

    List<Book> books = new ArrayList<>();
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

  private Book buildBookObjectFromJsonNode(JsonNode bookNode, JsonNode listNode, String isbn) {
    return Book.builder()
        .title(bookNode.path("title").asText())
        .author(bookNode.path("author").asText())
        .imageUrl(bookNode.path("book_image").asText())
        .isFavourite(false)
        .price(bookNode.path("price").asDouble())
        .listName(listNode.path("list_name").asText())
        .encodedListName(listNode.path("list_name_encoded").asText())
        .isbn(isbn)
        .build();
  }

  private String buildUri(String url) {
    return UriComponentsBuilder.fromUriString(url)
        .queryParam("api-key", configuration.getApiKey())
        .toUriString();
  }

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
              repository.save(book);
              log.info("Book with ISBN {} added to favourite list", book.getIsbn());
            });
  }

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
}

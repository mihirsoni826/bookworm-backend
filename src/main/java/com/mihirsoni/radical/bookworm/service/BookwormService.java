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
              Book book =
                  Book.builder()
                      .title(bookNode.path("title").asText())
                      .author(bookNode.path("author").asText())
                      .imageUrl(bookNode.path("book_image").asText())
                      .isFavourite(false)
                      .price(bookNode.path("price").asDouble())
                      .listName(listNode.path("list_name").asText())
                      .encodedListName(listNode.path("list_name_encoded").asText())
                      .isbn(bookNode.path("primary_isbn13").asText())
                      .build();
              books.add(book);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return books;
  }

  private String buildUri(String url) {
    return UriComponentsBuilder.fromUriString(url)
        .queryParam("api-key", configuration.getApiKey())
        .toUriString();
  }
}

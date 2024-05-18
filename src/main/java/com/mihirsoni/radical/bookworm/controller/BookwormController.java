package com.mihirsoni.radical.bookworm.controller;

import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.service.BookwormService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/books")
@AllArgsConstructor
@Slf4j
public class BookwormController {

  private BookwormService service;

  @GetMapping("/get-all-bestsellers")
  public List<Book> fetchAllBestsellers() {
    log.info("Request received to fetch all categories from New York Times API");
    return service.fetchAllBestsellersAcrossCategoriesFromNYT();
  }
}

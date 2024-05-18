package com.mihirsoni.radical.bookworm.controller;

import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.service.BookwormService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/books")
@AllArgsConstructor
@Slf4j
public class BookwormController {

  private final BookwormService service;

  @GetMapping("/get-all-bestsellers")
  public List<Book> fetchAllBestsellers() {
    log.info("Request received to fetch all bestsellers from New York Times API");
    return service.fetchAllBestsellersAcrossCategoriesFromNYT();
  }

  @PostMapping("/add-to-favourite")
  public void addToFavourite(@RequestBody Book book) {
    log.info("Request received to add book with ISBN {} to favourite list", book.getIsbn());
    service.addToFavourite(book);
  }

  @DeleteMapping("/remove-from-favourite")
  public void removeFromFavourite(@RequestBody Book book) {
    log.info("Request received to remove book with ISBN {} from favourite list", book.getIsbn());
    service.removeFromFavourite(book);
  }

  @PutMapping("/update-rating-and-price")
  public void updateRatingAndPrice(@RequestBody Book book) {
    log.info("Request received to update rating and price of book with ISBN {}", book.getIsbn());
    service.updateRatingAndPrice(book);
  }
}

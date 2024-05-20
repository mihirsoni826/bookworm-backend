package com.mihirsoni.radical.bookworm.controller;

import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.service.BookwormService;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/books")
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class BookwormController {

  private final BookwormService service;

  @GetMapping("/get-all-bestsellers")
  public Set<Book> fetchAllBestsellers() {
    log.info("Request received to fetch all bestsellers from New York Times API");
    return service.fetchAllBestsellersAcrossCategoriesFromNYTAPI();
  }

  @PostMapping("/add-to-favourites")
  public void addToFavourite(@RequestBody Book book) {
    log.info("Request received to add book with ISBN {} to favourite list", book.getIsbn());
    service.addToFavourite(book);
  }

  @DeleteMapping("/remove-from-favourites")
  public void removeFromFavourite(@RequestBody Book book) {
    log.info("Request received to remove book with ISBN {} from favourite list", book.getIsbn());
    service.removeFromFavourite(book);
  }

  @PutMapping("/update-rating-and-price")
  public void updateRatingAndPrice(@RequestBody Book book) {
    log.info("Request received to update rating and price of book with ISBN {}", book.getIsbn());
    service.updateRatingAndPrice(book);
  }

  @GetMapping("/get-favourites")
  public List<Book> getFavouriteList() {
    log.info("Request received to fetch favourite list");
    return service.getFavouriteList();
  }

  @DeleteMapping("/purge-database")
  public void purgeDatabase() {
    log.info("Request received to purge database");
    service.purgeDatabase();
  }
}

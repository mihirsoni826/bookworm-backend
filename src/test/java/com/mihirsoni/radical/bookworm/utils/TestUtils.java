package com.mihirsoni.radical.bookworm.utils;

import com.mihirsoni.radical.bookworm.models.Book;

public class TestUtils {
  public static Book createBook(
      int rating, int price, boolean isFavourite, boolean isRatingPriceChanged) {
    return Book.builder()
        .isbn("123")
        .title("ABC")
        .author("XYZ")
        .price(price)
        .rating(rating)
        .imageUrl("nowhere.png")
        .isFavourite(isFavourite)
        .isRatingPriceChanged(isRatingPriceChanged)
        .listName("List A")
        .encodedListName("list-a")
        .build();
  }
}

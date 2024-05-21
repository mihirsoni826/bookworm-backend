package com.mihirsoni.radical.bookworm.controller;

import com.mihirsoni.radical.bookworm.models.Book;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Set;

public interface BookwormApi {
  @Operation(
      summary = "Fetch all bestsellers",
      description =
          "Fetches all bestsellers from the New York Times API and the database and returns a unique set of books")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  Set<Book> fetchAllBestsellers();

  @Operation(
      summary = "Adds a book to favourite list",
      description =
          "Adds a book to favourite list and takes no action if it already exists in the list")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  void addToFavourite(Book book);

  @Operation(
      summary = "Removes a book from favourite list",
      description =
          "Removes a book from favourite list and takes no action if it doesn't exist in the list")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  void removeFromFavourite(Book book);

  @Operation(
      summary = "Updates rating and price of a book",
      description =
          "Updates rating and price of a book if it already exists, otherwise saves the book in the database")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  void updateRatingAndPrice(Book book);

  @Operation(
      summary = "Fetch all favourite books",
      description = "Fetches all favourite books from the database")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  List<Book> getFavouriteList();

  @Operation(
      summary = "Purges the database",
      description = "Purges the database to reset user experience")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success")})
  void purgeDatabase();
}

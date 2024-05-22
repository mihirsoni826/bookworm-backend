package com.mihirsoni.radical.bookworm.controller;

import static com.mihirsoni.radical.bookworm.utils.TestUtils.createBook;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihirsoni.radical.bookworm.models.Book;
import com.mihirsoni.radical.bookworm.service.BookwormService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BookwormController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class BookwormControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private BookwormService service;
  @Spy ObjectMapper objectMapper = new ObjectMapper();

  /** Checks if get-all-bestsellers call return OK status for happy path */
  @Test
  void BookwormController_GetAllBestsellers_ReturnsSetOfBestsellers() throws Exception {
    Book book1 = createBook(3, 10, true, true);

    when(service.fetchAllBestsellersAcrossCategoriesFromNYTAPI()).thenReturn(Set.of(book1));

    ResultActions response = mockMvc.perform(get("/api/v1/books/get-all-bestsellers"));

    response.andExpect(MockMvcResultMatchers.status().isOk());
  }

  /** Checks if add-to-favourites call return OK status for happy path */
  @Test
  void BookwormController_AddToFavourites_AddsBookToFavourites() throws Exception {
    Book book = createBook(3, 10, false, false);
    doNothing().when(service).addToFavourite(any());

    ResultActions response =
        mockMvc.perform(
            post("/api/v1/books/add-to-favourites")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(book)));

    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(""));
  }

  /** Checks if remove-from-favourites call return OK status for happy path */
  @Test
  void BookwormController_RemoveFromFavourites_RemovesBookFromFavourites() throws Exception {
    doNothing().when(service).removeFromFavourite(any(Book.class));

    ResultActions response =
        mockMvc.perform(
            delete("/api/v1/books/remove-from-favourites")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(createBook(3, 10, true, true))));

    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(""));
  }

  /** Checks if update-rating-and-price call return OK status for happy path */
  @Test
  void BookwormController_UpdateRatingAndPrice_UpdatesBook() throws Exception {
    doNothing().when(service).updateRatingAndPrice(any(Book.class));

    ResultActions response =
        mockMvc.perform(
            put("/api/v1/books/update-rating-and-price")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(createBook(3, 10, true, true))));

    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(""));
  }

  /** Checks if purge-database call return OK status for happy path */
  @Test
  void BookwormController_PurgeDatabase_PurgesDatabase() throws Exception {
    doNothing().when(service).purgeDatabase();

    ResultActions response = mockMvc.perform(delete("/api/v1/books/purge-database"));

    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(""));
  }
}

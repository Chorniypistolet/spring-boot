package mate.academy.spring.boot.controllerTests;

import mate.academy.spring.boot.dto.book.BookDto;
import mate.academy.spring.boot.dto.book.BookSearchParameters;
import mate.academy.spring.boot.dto.book.CreateBookRequestDto;
import mate.academy.spring.boot.dto.book.UpdateBookRequestDto;
import mate.academy.spring.boot.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookServiceImpl bookService;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("Should return List BookDtoS")
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllBooks_shouldReturnListOfBooksDto() throws Exception{
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = new BookDto();
        firstBook.setTitle("Book 1");
        firstBook.setAuthor("Author 1");
        firstBook.setPrice(BigDecimal.valueOf(20.00));
        expected.add(firstBook);
        BookDto secondBook = new BookDto();
        secondBook.setTitle("Book 2");
        secondBook.setAuthor("Author 2");
        secondBook.setPrice(BigDecimal.valueOf(33.00));
        expected.add(secondBook);
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
        Assertions.assertNotNull(actual);
        Assertions.assertFalse(actual.isEmpty());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Should return book by ID")
    @WithMockUser
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
    , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
    , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetFindById_withCorrectId_shouldReturnBook() throws Exception {
        BookDto expected = new BookDto();
        expected.setTitle("Book 1");
        expected.setAuthor("Author 1");
        expected.setPrice(BigDecimal.valueOf(20.00));
        Long bookId = 1L;
        MvcResult result = mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    @DisplayName("Should return EntityNotFoundException")
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetBookById_withIncorrectId_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;
        mockMvc.perform(get("/books/{id}", invalidBookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should search books by title")
    @WithMockUser
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByTitle_withCorrectTitle_shouldReturnListOfBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = new BookDto();
        firstBook.setTitle("Book 1");
        firstBook.setAuthor("Author 1");
        firstBook.setPrice(BigDecimal.valueOf(20.00));
        expected.add(firstBook);
        String title = "Book 1";
        MvcResult result = mockMvc.perform(get("/books/by-title")
                        .param("title", title))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
        Assertions.assertNotNull(actual);
        Assertions.assertFalse(actual.isEmpty());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Searching books by InvalidTitle")
    @WithMockUser
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByTitle_ShouldReturnEmpty_WhenNoBooksFound() throws Exception {
        String nonExistentTitle = "Nonexistent Title";
        mockMvc.perform(get("/books/by-title")
                .param("title", nonExistentTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should create new book, and return BookDto")
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateBook_WithValidRequest_ShouldCreateAndReturnBook() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("New Book");
        requestDto.setAuthor("Author Name");
        requestDto.setIsbn("isbn");
        requestDto.setPrice(BigDecimal.valueOf(34));
        BookDto expected = new BookDto();
        expected.setTitle("New Book");
        expected.setAuthor("Author Name");
        expected.setIsbn("isbn");
        expected.setPrice(BigDecimal.valueOf(34));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Adding a book with incorrect parameters,should return BadRequest")
    public void testCreateBook_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto();
        invalidRequest.setTitle("");
        invalidRequest.setPrice(BigDecimal.valueOf(-1));
        mockMvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should delete book successfully and return NoContent")
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteBook_WhenBookExists_ShouldReturnNoContent() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Search for books by the author parameter, we are waiting List of BookDto")
    public void testSearchBooks_BySearchParameters_ShouldReturnListOfBookDtos() throws Exception {
        String[] authors = {"Author"};
        BookSearchParameters searchParams = new BookSearchParameters(null, authors, null);
        String jsonParams = objectMapper.writeValueAsString(searchParams);
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = new BookDto();
        firstBook.setTitle("Book 1");
        firstBook.setAuthor("Author 1");
        firstBook.setPrice(BigDecimal.valueOf(20.00));
        expected.add(firstBook);
        BookDto secondBook = new BookDto();
        secondBook.setTitle("Book 2");
        secondBook.setAuthor("Author 2");
        secondBook.setPrice(BigDecimal.valueOf(33.00));
        MvcResult result = mockMvc.perform(get("/books/search")
                        .content(jsonParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
        Assertions.assertNotNull(actual);
        Assertions.assertFalse(actual.isEmpty());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Should return an empty list when no books match the search parameters")
    public void searchBooks_ShouldReturnEmpty_WhenNoBooksMatchSearchParams() throws Exception {
        String[] authors = {"Nonexistent Title"};
        mockMvc.perform(get("/books/search")
                        .param("title", authors))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Should update book by ID")
    public void testUpdateBook_WithAdminRole_ShouldUpdateAndReturnBookDto() throws Exception {
        Long bookId = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setTitle("UpdatedTitle");
        requestDto.setPrice(BigDecimal.valueOf(23));
        BookDto expected = new BookDto();
        expected.setPrice(BigDecimal.valueOf(23));
        expected.setTitle("UpdatedTitle");
        expected.setAuthor("Author 1");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/add-books-to-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-books-from-books-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Updating a Book, when Book not found,should return BadRequest")
    public void testUpdateBook_WhenBookNotFound_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setTitle("Updated Title");
        mockMvc.perform(put("/books/{id}", invalidBookId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }
}

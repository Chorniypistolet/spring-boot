package mate.academy.spring.boot.controllerTests;

import lombok.SneakyThrows;
import mate.academy.spring.boot.dto.book.BookDto;
import mate.academy.spring.boot.dto.book.BookSearchParameters;
import mate.academy.spring.boot.dto.book.CreateBookRequestDto;
import mate.academy.spring.boot.dto.book.UpdateBookRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-to-books-table.sql"));
        }
    }

    @AfterEach
    void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-books-from-books-table.sql"));
        }
    }

    @Test
    @DisplayName("Should return List BookDtoS")
    @WithMockUser
    public void testGetAllBooks_shouldReturnListOfBooksDto() throws Exception{
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = getBookDto("Book 1", "Author 1", BigDecimal.valueOf(20.00));
        expected.add(firstBook);
        BookDto secondBook = getBookDto("Book 2", "Author 2", BigDecimal.valueOf(33.00));
        expected.add(secondBook);

        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Should return book by ID")
    @WithMockUser
    public void testGetFindById_withCorrectId_shouldReturnBook() throws Exception {
        Long bookId = 1L;
        String title = "Book 1";
        String author = "Author 1";
        BigDecimal price = BigDecimal.valueOf(20.00);
        BookDto expected = getBookDto(title, author, price);
        MvcResult result = mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "price"));

    }

    @Test
    @DisplayName("Should return EntityNotFoundException")
    @WithMockUser
    public void testGetBookById_withIncorrectId_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;
        int expected = HttpStatus.NOT_FOUND.value();
        MvcResult result = mockMvc.perform(get("/books/{id}", invalidBookId))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should search books by title")
    @WithMockUser
    public void getBooksByTitle_withCorrectTitle_shouldReturnListOfBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = getBookDto("Book 1", "Author 1", BigDecimal.valueOf(20.00));
        expected.add(firstBook);
        String title = "Book 1";

        MvcResult result = mockMvc.perform(get("/books/by-title")
                        .param("title", title))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Searching books by InvalidTitle")
    @WithMockUser
    public void getBooksByTitle_ShouldReturnEmpty_WhenNoBooksFound() throws Exception {
        String nonExistentTitle = "NonExistentTitle";
        List<BookDto> expected = Collections.emptyList();

        MvcResult result = mockMvc.perform(get("/books/by-title")
                        .param("title", nonExistentTitle))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<BookDto> actual = objectMapper.readValue(
                responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class)
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should create new book, and return BookDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateBook_WithValidRequest_ShouldCreateAndReturnBook() throws Exception {
        CreateBookRequestDto requestDto = getCreateBookRequestDto(
                BigDecimal.valueOf(34));
        BookDto expected = getBookDto("New Book", "Author Name", BigDecimal.valueOf(34));
        expected.setIsbn("isbn");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "categories"));
    }

    @Test
    @DisplayName("Adding a book with incorrect parameters,should return BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateBook_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateBookRequestDto invalidRequest = new CreateBookRequestDto();
        invalidRequest.setTitle("");
        invalidRequest.setPrice(BigDecimal.valueOf(-1));

        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actual = result.getResponse().getContentAsString();

        assertFalse(actual.isEmpty());
    }

    @Test
    @DisplayName("Should delete book successfully and return NoContent")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteBook_WhenBookExists_ShouldReturnNoContent() throws Exception {
        Long bookId = 1L;

        MvcResult result = mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isNoContent())
                .andReturn();
        String actual = result.getResponse().getContentAsString();

        assertTrue(actual.isEmpty());
    }


    @Test
    @DisplayName("Should return 404 Not Found when trying to fetch a deleted non existing book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getDeletedBook_WhenBookDoestNotExis_ShouldReturnNotFound() throws Exception {
        Long bookId = 44L;
        int expected = HttpStatus.NOT_FOUND.value();

        MvcResult result = mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Updating a Book, when Book not found,should return BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBook_WhenBookNotFound_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setTitle("Updated Title");
        int expected = HttpStatus.NOT_FOUND.value();

        MvcResult result = mockMvc.perform(put("/books/{id}", invalidBookId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Search for books by the author parameter, we are waiting List of BookDto")
    @WithMockUser
    public void testSearchBooks_BySearchParameters_ShouldReturnListOfBookDtos() throws Exception {
        String[] authors = {"Author"};
        BookSearchParameters searchParams = new BookSearchParameters(null, authors, null);
        String jsonParams = objectMapper.writeValueAsString(searchParams);
        List<BookDto> expected = List.of(
                getBookDto("Book 1", "Author 1", BigDecimal.valueOf(20.00)),
                getBookDto("Book 2", "Author 2", BigDecimal.valueOf(33.00))
        );

        MvcResult result = mockMvc.perform(get("/books/search")
                        .content(jsonParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            BookDto expectedBook = expected.get(i);
            BookDto actualBook = actual.get(i);

            assertEquals(expectedBook.getTitle(), actualBook.getTitle());
            assertEquals(expectedBook.getAuthor(), actualBook.getAuthor());

            if (expectedBook.getPrice() != null && actualBook.getPrice() != null) {
                assertEquals(0, expectedBook.getPrice().compareTo(actualBook.getPrice()));
            }

            assertEquals(expectedBook.getDescription(), actualBook.getDescription());
            assertEquals(expectedBook.getCoverImage(), actualBook.getCoverImage());

            assertTrue(
                    (expectedBook.getCategories() == null && (actualBook.getCategories() == null || actualBook.getCategories().isEmpty())) ||
                            (expectedBook.getCategories() != null && expectedBook.getCategories().equals(actualBook.getCategories()))
            );
        }
    }

    @Test
    @DisplayName("Should return an empty list when no books match the search parameters")
    @WithMockUser
    public void searchBooks_ShouldReturnEmpty_WhenNoBooksMatchSearchParams() throws Exception {
        String[] authors = {"Nonexistent Author"};
        List<BookDto> expected = Collections.emptyList();

        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("title", authors))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        List<BookDto> actual = objectMapper.readValue(
                responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class)
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should update book by ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBook_WithAdminRole_ShouldUpdateAndReturnBookDto() throws Exception {
        Long bookId = 1L;
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        requestDto.setTitle("UpdatedTitle");
        requestDto.setPrice(BigDecimal.valueOf(23));
        BookDto expected = getBookDto("UpdatedTitle", "Author 1", BigDecimal.valueOf(23));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertEquals(0, expected.getPrice().compareTo(actual.getPrice()));
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "price"));
    }

    private BookDto getBookDto(String title, String author, BigDecimal price) {
        BookDto bookDto = new BookDto();

        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setPrice(price);
        bookDto.setCategories(Set.of());
        return bookDto;
    }

    private CreateBookRequestDto getCreateBookRequestDto(BigDecimal price) {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setTitle("New Book");
        createBookRequestDto.setAuthor("Author Name");
        createBookRequestDto.setPrice(price);
        createBookRequestDto.setIsbn("isbn");
        return createBookRequestDto;
    }
}

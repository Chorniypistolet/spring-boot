package mate.academy.spring.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.spring.boot.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.boot.dto.category.CategoryDto;
import mate.academy.spring.boot.dto.category.CategoryRequestDto;
import org.junit.jupiter.api.TestInfo;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext,
            TestInfo testInfo
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        if (testInfo.getDisplayName().equals("Should return books by category ID when category exists")) {
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
            new ClassPathResource("database/add-categories-to-categories-table.sql"));
        }
    }

    @AfterEach
    void afterAll(
            @Autowired DataSource dataSource,
            TestInfo testInfo
    ) {
        if (testInfo.getDisplayName().equals("Should return books by category ID when category exists")) {
            return;
        }
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
            connection,
            new ClassPathResource("database/remove-categories-from-categories-table.sql")
            );
        }
    }

    @Test
    @DisplayName("Should create new category, and return CategoryDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddCategory_WithValidRequest_ShouldCreateAndReturnCategoryDto() throws Exception {
        CategoryRequestDto categoryRequestDto = getCategoryRequestDto("Test Category");
        categoryRequestDto.setDescription("Test Description");
        CategoryDto expected = getCategoryDto("Test Category");
        expected.setDescription("Test Description");
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Adding a Category with incorrect parameters,should return BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddCategory_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setDescription("Test Description");

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = result.getResponse().getContentAsString();

        assertFalse(actualResponse.isEmpty());
    }

    @Test
    @DisplayName("Should return List BookDtoS")
    @WithMockUser
    public void testGetAll_shouldReturnListOfCategoryDto() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        CategoryDto firstCategory = getCategoryDto("Fiction");
        expected.add(firstCategory);
        CategoryDto secondCategory = getCategoryDto("Comedy");
        expected.add(secondCategory);

        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Should update category, and return CategoryDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateCategory_WithValidRequest_ShouldUpdateAndReturnCategoryDto() throws Exception {
        Long categoryId = 1L;
        CategoryRequestDto categoryRequestDto = getCategoryRequestDto("NewTitle");
        CategoryDto expected = getCategoryDto("NewTitle");
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Updating a Category, when category not found,should return NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateCategory_WhenCategoryNotFound_ShouldReturnNotFound() throws Exception {
        Long invalidCategoryId = 999L;
        CategoryRequestDto categoryRequestDto = getCategoryRequestDto("NewTitle");
        int expected = HttpStatus.NOT_FOUND.value();

        MvcResult result = mockMvc.perform(put("/categories/{id}", invalidCategoryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Searching a Category by ID, should return CategoryDto")
    @WithMockUser
    public void testGetCategoryById_WithValidId_ShouldReturnCategoryDto() throws Exception {
        Long categoryId = 1L;
        CategoryDto expected = getCategoryDto("Fiction");

        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Searching a Category by invalid ID, should return Not Found")
    @WithMockUser
    public void testGetCategoryById_withIncorrectId_ShouldReturnNotFound() throws Exception {
        Long invalidCategoryId = 999L;

        int expected = HttpStatus.NOT_FOUND.value();
        MvcResult result = mockMvc.perform(get("/categories/{id}", invalidCategoryId))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should return books by category ID when category exists")
    @Sql(scripts = {
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-categories-to-categories-table.sql",
            "classpath:database/add-books-categories-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-books-categories-from-books-categories-table.sql",
            "classpath:database/remove-categories-from-categories-table.sql",
            "classpath:database/remove-books-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser
    public void testGetBooksByCategoryId_WhenCategoryExists_ShouldReturnBooks() throws Exception {
        Long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        BookDtoWithoutCategoryIds book1 = getBookDtoWithoutCategoryIds("Book 1", "Author 1",
                BigDecimal.valueOf(20));
        expected.add(book1);
        BookDtoWithoutCategoryIds book2 = getBookDtoWithoutCategoryIds("Book 2", "Author 2",
                BigDecimal.valueOf(33));
        expected.add(book2);

        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDtoWithoutCategoryIds.class)
        );

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Should return empty list when category does not exist")
    @WithMockUser
    public void testGetBooksByCategoryId_WhenCategoryDoesNotExist_ShouldReturnEmptyList() throws Exception {
        Long nonExistentCategoryId = 88L;
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();

        MvcResult result = mockMvc.perform(get("/categories/{id}/books", nonExistentCategoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDtoWithoutCategoryIds.class)
        );

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should delete the category successfully and return 204 No Content")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteCategory_WhenCategoryExists_ShouldReturnNoContent() throws Exception {
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent())
                .andReturn();
        String actualResponse = result.getResponse().getContentAsString();

        assertTrue(actualResponse.isEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when trying to fetch a deleted category")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getDeletedCategory_WhenCategoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        Long categoryId = 4L;

        int expected = HttpStatus.NOT_FOUND.value();
        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    private CategoryDto getCategoryDto(String title) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(title);
        return categoryDto;
    }

    private BookDtoWithoutCategoryIds getBookDtoWithoutCategoryIds(String title, String author, BigDecimal price) {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setTitle(title);
        bookDtoWithoutCategoryIds.setAuthor(author);
        bookDtoWithoutCategoryIds.setPrice(price);
        return bookDtoWithoutCategoryIds;
    }

    private CategoryRequestDto getCategoryRequestDto(String title) {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName(title);
        return categoryRequestDto;
    }
}

package mate.academy.spring.boot.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.spring.boot.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.boot.dto.category.CategoryDto;
import mate.academy.spring.boot.dto.category.CategoryRequestDto;
import mate.academy.spring.boot.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class CategoryControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryServiceImpl categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should create new category, and return CategoryDto")
    public void testAddCategory_WithValidRequest_ShouldCreateAndReturnCategoryDto() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Test Category");
        categoryRequestDto.setDescription("Test Description");
        CategoryDto expected = new CategoryDto();
        expected.setName("Test Category");
        expected.setDescription("Test Description");
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Adding a Category with incorrect parameters,should return BadRequest")
    public void testAddCategory_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setDescription("Test Description");
        mockMvc.perform(post("/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return List BookDtoS")
    @Sql(scripts = "classpath:database/add-categories-to-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-categories-from-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAll_shouldReturnListOfCategoryDto() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        CategoryDto firstCategory = new CategoryDto();
        firstCategory.setName("Fiction");
        expected.add(firstCategory);
        CategoryDto secondCategory = new CategoryDto();
        secondCategory.setName("Comedy");
        expected.add(secondCategory);
        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class));
        Assertions.assertNotNull(actual);
        Assertions.assertFalse(actual.isEmpty());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should update category, and return CategoryDto")
    @Sql(scripts = "classpath:database/add-categories-to-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-categories-from-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateCategory_WithValidRequest_ShouldUpdateAndReturnCategoryDto() throws Exception {
        Long categoryId = 1L;
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("NewTitle");
        CategoryDto expected = new CategoryDto();
        expected.setName("NewTitle");
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);
        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Updating a Category, when category not found,should return NotFound")
    @Sql(scripts = "classpath:database/add-categories-to-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-categories-from-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateCategory_WhenCategoryNotFound_ShouldReturnNotFound() throws Exception {
        Long incorrectId = 999L;
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("NewTitle");
        mockMvc.perform(put("/categories/{id}", incorrectId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Searching a Category by ID, should return CategoryDto")
    @Sql(scripts = "classpath:database/add-categories-to-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-categories-from-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetCategoryById_WithValidId_ShouldReturnCategoryDto() throws Exception {
        Long categoryId = 1L;
        CategoryDto expected = new CategoryDto();
        expected.setName("Fiction");
        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    @DisplayName("Searching a Category by invalid ID, should return Not Found")
    public void testGetCategoryById_withIncorrectId_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;
        mockMvc.perform(get("/categories/{id}", invalidBookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return books by category ID when category exists")
    @Sql(scripts = {
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-categories-to-categories-table.sql",
            "classpath:database/add-books-categories-to-books-categories-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-books-categories-from-books-categories-table.sql",
            "classpath:database/remove-categories-from-categories-table.sql",
            "classpath:database/remove-books-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetBooksByCategoryId_WhenCategoryExists_ShouldReturnBooks() throws Exception {
        Long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        BookDtoWithoutCategoryIds book1 = new BookDtoWithoutCategoryIds();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setPrice(BigDecimal.valueOf(20));
        expected.add(book1);
        BookDtoWithoutCategoryIds book2 = new BookDtoWithoutCategoryIds();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setPrice(BigDecimal.valueOf(33));
        expected.add(book2);
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDtoWithoutCategoryIds.class)
        );
        Assertions.assertNotNull(actual);
        Assertions.assertFalse(actual.isEmpty());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when category does not exist")
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
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(actual.isEmpty());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should delete category successfully and return NoContent")
    @Sql(scripts = "classpath:database/add-categories-to-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-categories-from-categories-table.sql"
            , executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteCategory_WhenCategoryExists_ShouldReturnNoContent() throws Exception {
        Long categoryId = 1L;
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }
}

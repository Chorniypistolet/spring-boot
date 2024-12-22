package mate.academy.spring.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.spring.boot.dto.cartItem.CartItemDto;
import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
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
                    new ClassPathResource("database/add-users-to-users-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-shopping-carts-to-shopping-carts-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-cart-items-to-cart-items-table.sql"));
        }
    }

    @BeforeEach
    void setup() {
        User mockUser = new User();
        mockUser.setId(3L);
        mockUser.setEmail("user1@example.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, List.of())
        );
    }

    @AfterEach
    void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-cart-items-from-cart-items-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-shopping-carts-from-shopping-carts-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-books-from-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-users-from-users-table.sql"));
        }
    }

    @Test
    @DisplayName("Request to add book to ShoppingCart with valid request , we are waiting ShoppingCartDto")
    public void testAddBookToCart_whenValidInput_shouldReturnShoppingCartDto() throws Exception {
        Long userId = 3L;
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setBookId(2L);
        requestDto.setQuantity(1);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(3L);
        expected.setUserId(userId);
        CartItemDto cartItemDtoFirst = getCartItemDto(1L, 1L, "Book 1", 2);
        CartItemDto cartItemDtoSecond = getCartItemDto(3L, 2L, "Book 2", 1);
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        cartItemDtoSet.add(cartItemDtoFirst);
        cartItemDtoSet.add(cartItemDtoSecond);
        expected.setCartItems(cartItemDtoSet);

        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(() -> String.valueOf(userId)))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Request to add book to ShoppingCart with invalid request , we are waiting status isBadRequest")
    public void testAddBookToCart_whenInvalidInput_shouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        CartItemRequestDto invalidRequestDto = new CartItemRequestDto();
        invalidRequestDto.setBookId(null);
        invalidRequestDto.setQuantity(-1);

        mockMvc.perform(post("/cart")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequestDto))
                        .principal(() -> String.valueOf(userId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Request to receive ShoppingCart from an authorized user, we are waiting ShoppingCartDto")
    public void getShoppingCart_whenUserIsAuthenticated_shouldReturnShoppingCartDto() throws Exception {
        ShoppingCartDto expected = getShoppingCartDto();

        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Request to receive ShoppingCart from an unauthorized user, we are waiting status Unauthorized")
    public void getShoppingCart_whenUserIsNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Request to update ShoppingCart with valid new quantity, we are waiting ShoppingCartDto")
    public void testUpdateCartItemQuantity_whenValidInput_shouldUpdateCartItemQuantity() throws Exception {
        int newQuantity = 4;
        ShoppingCartDto expected = getShoppingCartDto();
        Set<CartItemDto> cartItems = expected.getCartItems();
        CartItemDto cartItemToUpdate = cartItems.iterator().next();
        int oldQuantity = cartItemToUpdate.getQuantity();
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setQuantity(newQuantity);
        cartItemRequestDto.setBookId(1L);

        MvcResult result = mockMvc.perform(put("/cart/items/{id}", cartItemToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cartItemRequestDto)))
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        cartItemToUpdate.setQuantity(newQuantity);

        assertNotNull(actual);
        assertNotNull(actual.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());

        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        CartItemDto actualCartItem = actual.getCartItems().iterator().next();
        CartItemDto expectedCartItem = cartItemToUpdate;

        assertEquals(expectedCartItem.getId(), actualCartItem.getId());
        assertEquals(expectedCartItem.getBookId(), actualCartItem.getBookId());
        assertEquals(expectedCartItem.getBookTitle(), actualCartItem.getBookTitle());
        assertEquals(expectedCartItem.getQuantity(), actualCartItem.getQuantity());
        assertNotEquals(oldQuantity, cartItemToUpdate.getQuantity());
    }

    @Test
    @DisplayName("Request to update ShoppingCart with invalid new quantity, we are waiting status BAD_REQUEST")
    public void testUpdateCartItemQuantity_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        int invalidQuantity = -99;
        int expected = HttpStatus.BAD_REQUEST.value();

        ShoppingCartDto shoppingCartDto = getShoppingCartDto();
        Set<CartItemDto> cartItems = shoppingCartDto.getCartItems();
        CartItemDto cartItemToUpdate = cartItems.iterator().next();
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(1L);
        cartItemRequestDto.setQuantity(invalidQuantity);

        MvcResult result = mockMvc.perform(put("/cart/items/{id}", cartItemToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cartItemRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        int actual = result.getResponse().getStatus();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Request to delete ShoppingCart with valid cartItemId, we are waiting status isOk")
    public void testDeleteCartItem_whenValidInput_shouldDeleteCartItem() throws Exception {
        Long cartItemId = 1L;
        Long userId = 3L;

        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId)
                        .principal(() -> String.valueOf(userId)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Request to delete ShoppingCart with invalid cartItemId, we are waiting status isNotFound")
    public void testDeleteCartItem_whenCartItemNotFound_shouldReturnNotFound() throws Exception {
        Long cartItemId = 999L;
        Long userId = 3L;

        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId)
                        .principal(() -> String.valueOf(userId)))
                .andExpect(status().isNotFound());
    }

    private ShoppingCartDto getShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(3L);
        User user = createTestUser();
        shoppingCartDto.setUserId(user.getId());
        CartItemDto cartItemDto = createCartItemDto();
        Set<CartItemDto> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItemDto);
        shoppingCartDto.setCartItems(cartItemSet);
        return shoppingCartDto;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(3L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setShippingAddress("Address 1");
        user.setPassword("password1");
        user.setEmail("user1@example.com");
        return user;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setPrice(BigDecimal.valueOf(20));
        return book;
    }

    private CartItemDto createCartItemDto() {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(createBook().getId());
        cartItemDto.setBookTitle(createBook().getTitle());
        cartItemDto.setQuantity(2);
        return cartItemDto;
    }

    private CartItemDto getCartItemDto(Long id, Long bookId, String bookTitle, int quantity) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(id);
        cartItemDto.setBookId(bookId);
        cartItemDto.setBookTitle(bookTitle);
        cartItemDto.setQuantity(quantity);
        return cartItemDto;
    }
}

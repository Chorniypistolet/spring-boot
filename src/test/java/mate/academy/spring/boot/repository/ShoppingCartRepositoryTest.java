package mate.academy.spring.boot.repository;

import lombok.SneakyThrows;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource
    ) throws SQLException {
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
    @DisplayName("Test findByUserId should return shopping cart for existing user")
    void testFindByUserId_WithExistingUser_ShouldReturnShoppingCart() {
        Long userId = 3L;
        ShoppingCart expected = getShoppingCart();

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);
        assertTrue(result.isPresent());

        ShoppingCart actual = result.get();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());

        assertEquals(expected.getCartItemSet().size(), actual.getCartItemSet().size());

        expected.getCartItemSet().forEach(expectedCartItem -> {
            boolean matchFound = actual.getCartItemSet().stream()
                    .anyMatch(actualCartItem -> {
                        return expectedCartItem.getId().equals(actualCartItem.getId()) &&
                                expectedCartItem.getQuantity() == actualCartItem.getQuantity() &&
                                expectedCartItem.getBook().getId().equals(actualCartItem.getBook().getId());
                    });
            assertTrue(matchFound);
        });
    }

    @Test
    @DisplayName("Test findByIdWithItems should return empty for non-existing shopping cart")
    void testFindByIdWithItems_WithNonExistingCart_ShouldReturnEmpty() {
        Long nonExistingCartId = -1L;

        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(nonExistingCartId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findByIdWithItems should return shopping cart with items for an existing ID")
    void testFindByIdWithItems_WithExistingId_ShouldReturnShoppingCartWithItems() {
        Long id = 3L;
        ShoppingCart expected = getShoppingCart();

        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(id);
        assertTrue(result.isPresent());

        ShoppingCart actual = result.get();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());

        assertEquals(expected.getCartItemSet().size(), actual.getCartItemSet().size());

        expected.getCartItemSet().forEach(expectedCartItem -> {
            boolean matchFound = actual.getCartItemSet().stream()
                    .anyMatch(actualCartItem -> {
                        return expectedCartItem.getId().equals(actualCartItem.getId()) &&
                                expectedCartItem.getQuantity() == actualCartItem.getQuantity() &&
                                expectedCartItem.getBook().getId().equals(actualCartItem.getBook().getId());
                    });
            assertTrue(matchFound);
        });
    }

    @Test
    @DisplayName("Test findByIdWithItems should return empty for a non-existing ID")
    void testFindByIdWithItems_WithNonExistingId_ShouldReturnEmpty() {
        Long nonExistingCartId = -1L;

        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(nonExistingCartId);

        assertTrue(result.isEmpty());
    }

    private ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(3L);
        User user = createTestUser();
        shoppingCart.setUser(user);
        Set<CartItem> cartItemSet = new HashSet<>();
        CartItem cartItem = createCartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItemSet.add(cartItem);
        shoppingCart.setCartItemSet(cartItemSet);
        return shoppingCart;
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

    private CartItem createCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(createBook());
        cartItem.setQuantity(2);
        return cartItem;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setPrice(BigDecimal.valueOf(20));
        return book;
    }
}
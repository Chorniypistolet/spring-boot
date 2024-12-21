package mate.academy.spring.boot.repositoryTests;

import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Test findByUserId should return shopping cart for existing user")
    @Sql(scripts = {
            "classpath:database/add-users-to-users-table.sql",
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-shopping-carts-to-shopping-carts-table.sql",
            "classpath:database/add-cart-items-to-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-cart-items-from-cart-items-table.sql",
            "classpath:database/remove-shopping-carts-from-shopping-carts-table.sql",
            "classpath:database/remove-books-from-books-table.sql",
            "classpath:database/remove-users-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindByUserId_WithExistingUser_ShouldReturnShoppingCart() {
        Long userId = 3L;
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUser().getId());
    }

    @Test
    @DisplayName("Test findByIdWithItems should return empty for non-existing shopping cart")
    void testFindByIdWithItems_WithNonExistingCart_ShouldReturnEmpty() {
        System.out.println(shoppingCartRepository.findAll());
        Long nonExistingCartId = -1L;
        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(nonExistingCartId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findByIdWithItems should return shopping cart with items for an existing ID")
    @Sql(scripts = {
            "classpath:database/add-users-to-users-table.sql",
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-shopping-carts-to-shopping-carts-table.sql",
            "classpath:database/add-cart-items-to-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-cart-items-from-cart-items-table.sql",
            "classpath:database/remove-shopping-carts-from-shopping-carts-table.sql",
            "classpath:database/remove-books-from-books-table.sql",
            "classpath:database/remove-users-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindByIdWithItems_WithExistingId_ShouldReturnShoppingCartWithItems() {
        Long existingCartId = 3L;

        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(existingCartId);

        assertTrue(result.isPresent());
        ShoppingCart shoppingCart = result.get();
        assertEquals(existingCartId, shoppingCart.getId());
        assertFalse(shoppingCart.getCartItemSet().isEmpty());
    }

    @Test
    @DisplayName("Test findByIdWithItems should return empty for a non-existing ID")
    void testFindByIdWithItems_WithNonExistingId_ShouldReturnEmpty() {
        Long nonExistingCartId = -1L;

        Optional<ShoppingCart> result = shoppingCartRepository.findByIdWithItems(nonExistingCartId);

        assertTrue(result.isEmpty());
    }
}

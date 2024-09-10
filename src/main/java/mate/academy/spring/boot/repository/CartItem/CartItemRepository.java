package mate.academy.spring.boot.repository.CartItem;

import mate.academy.spring.boot.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.book WHERE ci.shoppingCart.id = :cartId")
    Set<CartItem> findByShoppingCartIdWithBook(@Param("cartId") Long cartId);

    Optional<CartItem> findByIdAndShoppingCartId(Long itemId, Long cartId);

}

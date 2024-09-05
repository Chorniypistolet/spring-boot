package mate.academy.spring.boot.repository.shoppingCart;

import mate.academy.spring.boot.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long userId);

    @Query("SELECT sc FROM ShoppingCart sc LEFT JOIN FETCH sc.cartItemSet WHERE sc.id = :id")
    Optional<ShoppingCart> findByIdWithItems(@Param("id") Long id);

}

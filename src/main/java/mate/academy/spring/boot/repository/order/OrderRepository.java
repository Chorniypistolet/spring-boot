package mate.academy.spring.boot.repository.order;

import mate.academy.spring.boot.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findOrderWithItems(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId")
    List<Order> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}

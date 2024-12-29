package mate.academy.spring.boot.repository.orderitem;

import java.util.List;
import mate.academy.spring.boot.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Query("UPDATE OrderItem oi SET oi.quantity = :quantity WHERE oi.id = :id")
    void updateOrderItemQuantity(@Param("id") Long id, @Param("quantity") int quantity);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.id = :itemId")
    OrderItem findByOrderIdAndItemId(@Param("orderId") Long orderId, @Param("itemId") Long itemId);
}

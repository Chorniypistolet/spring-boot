package mate.academy.spring.boot.service;

import java.util.List;
import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.orderitemdto.OrderItemDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    List<OrderDto> getAllOrdersByUserId(Long id, Pageable pageable);

    OrderDto updateStatus(Long id, StatusRequestDto statusRequestDto);

    OrderDto createOrder(Long userId, OrderRequestDto orderRequestDto);

    List<OrderItemDto> getItemsByOrderId(Long orderId);

    OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId);
}

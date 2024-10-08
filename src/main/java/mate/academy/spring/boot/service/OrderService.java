package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrdersByUserId(Long id, Pageable pageable);

    OrderDto updateStatus(Long id, StatusRequestDto statusRequestDto);

    OrderDto createOrder(Long userId, OrderRequestDto orderRequestDto);

    List<OrderItemDto> getItemsByOrderId(Long orderId);

    OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId);
}

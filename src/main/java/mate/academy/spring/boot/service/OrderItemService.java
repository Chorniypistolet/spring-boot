package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;

import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getItemsByOrderId(Long orderId);

    OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId);
}

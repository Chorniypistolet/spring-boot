package mate.academy.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.service.OrderItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllOrderItem (@PathVariable Long orderId) {
        return orderItemService.getItemsByOrderId(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemById(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getItemByOrderIdAndItemId(orderId, itemId);
    }
}

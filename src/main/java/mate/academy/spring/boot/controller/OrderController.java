package mate.academy.spring.boot.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.orderitemdto.OrderItemDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderDto makeAnOrder(@AuthenticationPrincipal User user,
                                @RequestBody @Valid OrderRequestDto orderRequestDto) {
        return orderService.createOrder(user.getId(), orderRequestDto);
    }

    @GetMapping
    public List<OrderDto> getAll(@AuthenticationPrincipal User user, Pageable pageable) {
        return orderService.getAllOrdersByUserId(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid StatusRequestDto statusRequestDto) {
        return orderService.updateStatus(id, statusRequestDto);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllOrderItem(@PathVariable Long orderId) {
        return orderService.getItemsByOrderId(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemById(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getItemByOrderIdAndItemId(orderId, itemId);
    }
}

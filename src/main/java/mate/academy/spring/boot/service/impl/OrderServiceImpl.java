package mate.academy.spring.boot.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import mate.academy.spring.boot.mapper.OrderMapper;
import mate.academy.spring.boot.model.Order;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.model.Status;
import mate.academy.spring.boot.repository.order.OrderRepository;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import mate.academy.spring.boot.service.OrderService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<OrderDto> getAllOrdersByUserId(Long id) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(id))
                .map(orderMapper :: toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, StatusRequestDto statusRequestDto) {
        Order order = orderRepository.findOrderWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException("No such a order with id: " + id));
        String statusString = statusRequestDto.getStatus();
        Status newStatus = Arrays.stream(Status.values())
                .filter(status -> status.name().equalsIgnoreCase(statusString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + statusString));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user " + userId));
        Order order = orderMapper.toOrder(shoppingCart);
        order.setTotal(getTotal(shoppingCart));
        order.setOrderTime(LocalDateTime.now());
        order.setUser(shoppingCart.getUser());
        order.setShippingAddress(orderRequestDto.getShippingAddress());
        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));
        order.setStatus(Status.PENDING);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    private BigDecimal getTotal(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItemSet().stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

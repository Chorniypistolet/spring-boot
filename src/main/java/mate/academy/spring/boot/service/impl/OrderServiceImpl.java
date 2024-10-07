package mate.academy.spring.boot.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import mate.academy.spring.boot.exception.OrderProcessingException;
import mate.academy.spring.boot.mapper.OrderItemMapper;
import mate.academy.spring.boot.mapper.OrderMapper;
import mate.academy.spring.boot.model.Order;
import mate.academy.spring.boot.model.OrderItem;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.model.Status;
import mate.academy.spring.boot.repository.order.OrderRepository;
import mate.academy.spring.boot.repository.orderItem.OrderItemRepository;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import mate.academy.spring.boot.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderDto> getAllOrdersByUserId(Long id, Pageable pageable) {
        return orderMapper.toOrderDtoList(orderRepository.findAllByUserId(id, pageable));
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, StatusRequestDto statusRequestDto) {
        Order order = orderRepository.findById(id)
                .map(o -> {
                    o.setStatus(Status.valueOf(statusRequestDto.getStatus()));
                    return o;
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with id: %d not found", id)
                ));
        return orderMapper.toUpdateDto(orderRepository.save(order));
    }

    @Override
    public OrderDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user " + userId));
        if (shoppingCart.getCartItemSet().isEmpty()) {
            throw new OrderProcessingException("CartItems not found ");
        }
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

    @Override
    public List<OrderItemDto> getItemsByOrderId(Long orderId) {
        List<OrderItem> byOrderId = orderItemRepository.findByOrderId(orderId);
        return byOrderId.stream()
                .map(orderItemMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId) {
        return orderItemMapper.toOrderItemDto(orderItemRepository.findByOrderIdAndItemId(orderId, itemId));
    }

    private BigDecimal getTotal(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItemSet().stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

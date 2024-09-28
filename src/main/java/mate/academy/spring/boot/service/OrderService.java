package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.dto.order.OrderRequestDto;
import mate.academy.spring.boot.dto.status.StatusRequestDto;
import mate.academy.spring.boot.model.User;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrdersByUserId(Long id);

    OrderDto updateStatus(Long id, StatusRequestDto statusRequestDto);

    OrderDto createOrder(Long userId, OrderRequestDto orderRequestDto);
}

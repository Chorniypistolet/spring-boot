package mate.academy.spring.boot.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.mapper.OrderItemMapper;
import mate.academy.spring.boot.model.OrderItem;
import mate.academy.spring.boot.repository.orderItem.OrderItemRepository;
import mate.academy.spring.boot.service.OrderItemService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

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
}

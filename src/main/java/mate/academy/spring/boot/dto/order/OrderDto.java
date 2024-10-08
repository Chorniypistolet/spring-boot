package mate.academy.spring.boot.dto.order;

import lombok.Data;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.model.Status;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private Status status;
}

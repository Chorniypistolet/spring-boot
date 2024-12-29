package mate.academy.spring.boot.dto.order;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import mate.academy.spring.boot.dto.orderitemdto.OrderItemDto;
import mate.academy.spring.boot.model.Status;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private Status status;
}

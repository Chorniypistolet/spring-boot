package mate.academy.spring.boot.dto.orderitemdto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private int quantity;
}


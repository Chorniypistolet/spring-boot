package mate.academy.spring.boot.dto.cartItem;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequestDto {
    @Positive
    private long bookId;
    @Positive
    private int quantity;
}

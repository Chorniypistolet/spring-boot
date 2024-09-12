package mate.academy.spring.boot.dto.shoppingCart;

import lombok.Data;
import mate.academy.spring.boot.dto.cartItem.CartItemDto;
import java.util.Set;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}

package mate.academy.spring.boot.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import mate.academy.spring.boot.dto.cartitem.CartItemDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}

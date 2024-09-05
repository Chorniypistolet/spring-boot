package mate.academy.spring.boot.dto.shoppingCart;

import lombok.Getter;
import lombok.Setter;
import mate.academy.spring.boot.dto.cartItem.CartItemDto;
import java.util.Set;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}

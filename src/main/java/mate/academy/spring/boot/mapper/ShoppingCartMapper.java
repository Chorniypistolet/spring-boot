package mate.academy.spring.boot.mapper;

import mate.academy.spring.boot.config.MapperConfig;
import mate.academy.spring.boot.dto.cartItem.CartItemDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "cartItemSet")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toCartItemDto(CartItem cartItem);
}

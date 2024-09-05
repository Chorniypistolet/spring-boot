package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto getCartByUserId(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateCartItemQuantity(Long cartItemId, Integer quantity);

    void deleteCartItem(Long id);

    void clearCart(Long userId);

}

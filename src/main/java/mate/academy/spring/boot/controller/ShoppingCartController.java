package mate.academy.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.service.ShoppingCartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    ShoppingCartDto addBookToCart(@AuthenticationPrincipal User user, @RequestBody CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.addBookToCart(user.getId(), cartItemRequestDto);
    }
    @GetMapping
    public ShoppingCartDto getShoppingCart (@AuthenticationPrincipal User user){
        return shoppingCartService.getCartByUserId(user.getId());
    }

    @PutMapping("/items/{id}")
    public ShoppingCartDto updateCartItemQuantity (@PathVariable("id") Long id,
            @RequestBody CartItemRequestDto cartItemRequestDto){
        return shoppingCartService.updateCartItemQuantity(id, cartItemRequestDto.getQuantity());
    }

    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem (@PathVariable Long id){
        shoppingCartService.deleteCartItem(id);
    }

    @DeleteMapping("/items")
    public void clearCart(@AuthenticationPrincipal User user) {
        shoppingCartService.clearCart(user.getId());
    }
}

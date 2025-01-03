package mate.academy.spring.boot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.cartitem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingcart.ShoppingCartDto;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public ShoppingCartDto addBookToCart(@AuthenticationPrincipal User user,
                                         @RequestBody @Valid
                                         CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.addBookToCart(user.getId(), cartItemRequestDto);
    }

    @GetMapping
    public ShoppingCartDto getShoppingCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.getCartByUserId(user.getId());
    }

    @PutMapping("/items/{id}")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ShoppingCartDto updateCartItemQuantity(@AuthenticationPrincipal User user,
                                                  @PathVariable("id") Long id,
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.updateCartItemQuantity(user.getId(),
                id, cartItemRequestDto.getQuantity());
    }

    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem(@AuthenticationPrincipal User user,
                               @PathVariable("cartItemId") Long id) {
        shoppingCartService.deleteCartItem(user.getId(), id);
    }
}

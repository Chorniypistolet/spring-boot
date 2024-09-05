package mate.academy.spring.boot.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.mapper.BookMapper;
import mate.academy.spring.boot.mapper.ShoppingCartMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.repository.CartItem.CartItemRepository;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import mate.academy.spring.boot.service.BookService;
import mate.academy.spring.boot.service.ShoppingCartService;
import mate.academy.spring.boot.service.UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;
    private final UserService userService;
    private final BookMapper bookMapper;

    @Override
    public ShoppingCartDto getCartByUserId(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found with userId " + userId));
        shoppingCart.setCartItemSet(cartItemRepository.findByShoppingCartIdWithBook(shoppingCart.getId()));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto) {
        User user = userService.findById(userId);
        ShoppingCart shoppingCart = shoppingCartRepository.findByIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not for user " + user));
        Book book = bookMapper.toEntity(bookService.findById(cartItemRequestDto.getBookId()));
        CartItem existingCartItem = shoppingCart.getCartItemSet()
                .stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequestDto.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setBook(book);
            newCartItem.setQuantity(cartItemRequestDto.getQuantity());
            newCartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItemSet().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateCartItemQuantity(Long id, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id " + id));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    public void deleteCartItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id " + id));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByIdWithItems(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for userId " + userId));;
        shoppingCart.getCartItemSet().clear();
        shoppingCartRepository.save(shoppingCart);
    }
}

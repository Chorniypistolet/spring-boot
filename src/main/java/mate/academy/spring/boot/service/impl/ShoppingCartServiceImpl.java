package mate.academy.spring.boot.service.impl;

import mate.academy.spring.boot.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.mapper.ShoppingCartMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.repository.CartItem.CartItemRepository;
import mate.academy.spring.boot.repository.book.BookRepository;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import mate.academy.spring.boot.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getCartByUserId(Long userId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Book book = bookRepository.findById(cartItemRequestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        shoppingCart.getCartItemSet().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingCartItem -> existingCartItem.setQuantity(existingCartItem.getQuantity()
                                + cartItemRequestDto.getQuantity()),
                        () -> addNewCartItemToCart(shoppingCart, book, cartItemRequestDto.getQuantity())
                );
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto updateCartItemQuantity(Long userId, Long id, Integer quantity) {
        ShoppingCart cart = getShoppingCartByUserId(userId);
        CartItem cartItem = findByIdAndShoppingCartId(id, cart.getId());
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public void deleteCartItem(Long userId, Long id) {
        ShoppingCart cart = getShoppingCartByUserId(userId);
        CartItem cartItem = findByIdAndShoppingCartId(id, cart.getId());
        cartItemRepository.delete(cartItem);
    }

    private void addNewCartItemToCart(ShoppingCart shoppingCart, Book book, int quantity) {
        CartItem newCartItem = new CartItem();
        newCartItem.setBook(book);
        newCartItem.setQuantity(quantity);
        newCartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItemSet().add(newCartItem);
        cartItemRepository.save(newCartItem);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("ShoppingCart not found for user " + userId));
    }

    private CartItem findByIdAndShoppingCartId(Long itemId, Long cartId) {
        return cartItemRepository.findByIdAndShoppingCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id " + itemId));
    }
}

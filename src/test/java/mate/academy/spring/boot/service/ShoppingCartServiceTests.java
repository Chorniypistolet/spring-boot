package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.cartItem.CartItemDto;
import mate.academy.spring.boot.dto.cartItem.CartItemRequestDto;
import mate.academy.spring.boot.dto.shoppingCart.ShoppingCartDto;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.mapper.ShoppingCartMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.ShoppingCart;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.repository.CartItem.CartItemRepository;
import mate.academy.spring.boot.repository.book.BookRepository;
import mate.academy.spring.boot.repository.shoppingCart.ShoppingCartRepository;
import mate.academy.spring.boot.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTests {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Should return ShoppingCartDto when cart exists for user Id")
    void testGetCartByUserId_WithCorrectId_ShouldReturnShoppingCartDto() {
        // Given
        Long userId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();
        ShoppingCartDto expected = new ShoppingCartDto();

        // When
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // Then
        ShoppingCartDto actual = shoppingCartService.getCartByUserId(userId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when cart does not exist for user ID")
    void testGetCartByUserId_WithIncorrectId_ShouldThrowEntityNotFoundException() {
        // Given
        Long userId = 1L;

        // When
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.getCartByUserId(userId));
    }


    @Test
    @DisplayName("Should add book to cart and return updated ShoppingCartDto")
    void testAddBookToCart_WithCorrectParameters_ShouldReturnShoppingCartDto() {
        // Given
        User user = createTestUser();
        Book book = createTestBook();
        CartItemRequestDto requestDto = createTestCartItemRequestDto(book.getId());
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItemSet(new HashSet<>());

        ShoppingCartDto expected = createExpectedShoppingCartDto(book, 2);

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // Then
        ShoppingCartDto actual = shoppingCartService.addBookToCart(user.getId(), requestDto);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book is not found")
    void testAddBookToCart_WhenBookNotFound_ShouldThrowException() {
        // Given
        User user = createTestUser();
        Long invalidBookId = 99L;
        CartItemRequestDto requestDto = createTestCartItemRequestDto(invalidBookId);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItemSet(new HashSet<>());

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.addBookToCart(user.getId(), requestDto));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when cart item is not found")
    void testUpdateCartItemQuantity_WhenCartItemNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        User user = createTestUser();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        Long invalidCartItemId = 99L;
        int newQuantity = 3;

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(invalidCartItemId, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.updateCartItemQuantity(user.getId(), invalidCartItemId, newQuantity));
    }

    @Test
    @DisplayName("Should update cart item quantity and return updated ShoppingCartDto")
    void testUpdateCartItemQuantity_WithCorrectQuantity_ShouldReturnUpdatedShoppingCartDto() {
        // Given
        User user = createTestUser();
        Book book = createTestBook();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItemSet(new HashSet<>());
        int quantity = 2;
        int newQuantity = 3;
        CartItem cartItem = createCartItem(1L, shoppingCart, book, quantity);

        ShoppingCartDto expected = createExpectedShoppingCartDto(book, newQuantity);

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId())).thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // Then
        ShoppingCartDto actual = shoppingCartService.updateCartItemQuantity(user.getId(), cartItem.getId(), newQuantity);
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertEquals(newQuantity, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should delete cart item when valid input is provided")
    void testDeleteCartItem_WithValidInput_ShouldDeleteCartItem() {
        // Given
        User user = createTestUser();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        Book book = createTestBook();
        CartItem cartItem = createCartItem(1L, shoppingCart, book, 2);
        shoppingCart.setCartItemSet(Set.of(cartItem));

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));

        // Then
        shoppingCartService.deleteCartItem(user.getId(), cartItem.getId());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when cart item is not found")
    void testDeleteCartItem_WhenCartItemNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        User user = createTestUser();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        Long invalidCartItemId = 99L;

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(invalidCartItemId, shoppingCart.getId()))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.deleteCartItem(user.getId(), invalidCartItemId));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when shopping cart is not found")
    void testDeleteCartItem_WhenShoppingCartNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        User user = createTestUser();
        Long cartItemId = 1L;

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.deleteCartItem(user.getId(), cartItemId));
    }

    private ShoppingCartDto createExpectedShoppingCartDto(Book book, int quantity) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookId(book.getId());
        cartItemDto.setBookTitle(book.getTitle());
        cartItemDto.setQuantity(quantity);
        Set<CartItemDto> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItemDto);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        shoppingCartDto.setCartItems(cartItemSet);
        return shoppingCartDto;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("TestFirstName");
        user.setLastName("TestLastName");
        user.setEmail("test@test.com");
        return user;
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(BigDecimal.valueOf(29.99));
        return book;
    }

    private CartItemRequestDto createTestCartItemRequestDto(Long bookId) {
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setBookId(bookId);
        requestDto.setQuantity(2);
        return requestDto;
    }

    private CartItem createCartItem (Long id, ShoppingCart shoppingCart, Book book, int quantity){
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}

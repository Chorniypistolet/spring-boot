package mate.academy.spring.boot.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.spring.boot.config.MapperConfig;
import mate.academy.spring.boot.dto.order.OrderDto;
import mate.academy.spring.boot.model.CartItem;
import mate.academy.spring.boot.model.Order;
import mate.academy.spring.boot.model.OrderItem;
import mate.academy.spring.boot.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDto toDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orderList);

    @Named("toUpdateDto")
    OrderDto toUpdateDto(Order save);

    @Mapping(source = "cartItemSet", target = "orderItems")
    @Mapping(source = "user", target = "user")
    default Order toOrder(ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setOrderItems(toOrderItems(shoppingCart.getCartItemSet(), order));
        return order;
    }

    default Set<OrderItem> toOrderItems(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> toOrderItem(cartItem, order))
                .collect(Collectors.toSet());
    }

    default OrderItem toOrderItem(CartItem cartItem, Order order) {
        if (cartItem == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        BigDecimal price = cartItem.getBook().getPrice();
        if (price == null) {
            throw new IllegalArgumentException("CartItem book price cannot be null");
        }
        orderItem.setPrice(price);
        orderItem.setOrder(order);
        return orderItem;
    }
}

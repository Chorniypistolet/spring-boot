package mate.academy.spring.boot.mapper;

import mate.academy.spring.boot.config.MapperConfig;
import mate.academy.spring.boot.dto.orderItemDto.OrderItemDto;
import mate.academy.spring.boot.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}

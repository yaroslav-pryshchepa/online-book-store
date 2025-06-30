package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.OrderDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.Order;
import book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {

    @Mapping(source = "user.email", target = "userEmail")
    OrderDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}

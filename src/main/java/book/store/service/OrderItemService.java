package book.store.service;

import book.store.dto.orderitem.OrderItemDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getOrderItemsByOrderId(Long orderId);

    OrderItemDto getOrderItemById(Long orderId, Long itemId);
}

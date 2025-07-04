package book.store.service;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto createOrder(AddOrderRequestDto dto, Authentication authentication);

    Page<OrderDto> getOrders(Pageable pageable, Authentication authentication);

    OrderDto updateStatus(Long id, UpdateStatusRequestDto dto);

    List<OrderItemDto> getOrderItemsByOrderId(Long orderId);

    OrderItemDto getOrderItemById(Long orderId, Long itemId);
}

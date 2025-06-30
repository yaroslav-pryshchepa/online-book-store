package book.store.service;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto addOrder(AddOrderRequestDto dto);

    Page<OrderDto> getOrders(Pageable pageable);

    OrderDto updateStatus(Long id, UpdateStatusRequestDto dto);
}

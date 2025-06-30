package book.store.service.impl;

import book.store.dto.orderitem.OrderItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderMapper;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.service.OrderItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id "
                        + orderId));
        return order.getOrderItems().stream()
                .map(orderMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemById(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id "
                        + orderId));
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with id "
                        + itemId));
        return orderMapper.toOrderItemDto(orderItem);
    }
}

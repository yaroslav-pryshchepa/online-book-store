package book.store.controller;

import book.store.dto.orderitem.OrderItemDto;
import book.store.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Item management", description = "Endpoints for retrieving order items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders/{orderId}/items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get all order items for an order",
            description = "Retrieve all OrderItems associated with a specific order")
    public List<OrderItemDto> getOrderItems(@PathVariable Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{itemId}")
    @Operation(summary = "Get specific order item",
            description = "Retrieve a specific OrderItem within an order by its id")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getOrderItemById(orderId, itemId);
    }
}

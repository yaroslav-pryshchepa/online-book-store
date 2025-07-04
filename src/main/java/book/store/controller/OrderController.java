package book.store.controller;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order to purchase the books in user's shopping cart")
    public OrderDto createOrder(
            @RequestBody @Valid AddOrderRequestDto addOrderRequestDto,
            Authentication authentication) {
        return orderService.createOrder(addOrderRequestDto, authentication);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get user's order history",
            description = "Retrieve user's order history")
    public Page<OrderDto> getOrders(Pageable pageable,
            Authentication authentication) {
        return orderService.getOrders(pageable, authentication);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update order status", description = "Update order status by id")
    public OrderDto update(@PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequestDto requestDto) {
        return orderService.updateStatus(id, requestDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all order items for an order",
            description = "Retrieve all OrderItems associated with a specific order")
    public List<OrderItemDto> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get specific order item",
            description = "Retrieve a specific OrderItem within an order by its ID")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItemById(orderId, itemId);
    }
}

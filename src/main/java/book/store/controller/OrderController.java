package book.store.controller;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public OrderDto addOrder(
            @RequestBody @Valid AddOrderRequestDto addOrderRequestDto) {
        return orderService.addOrder(addOrderRequestDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get user's order history",
            description = "Retrieve user's order history")
    public Page<OrderDto> getOrders(Pageable pageable) {
        return orderService.getOrders(pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update order status", description = "Update order status by id")
    public OrderDto update(@PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequestDto requestDto) {
        return orderService.updateStatus(id, requestDto);
    }
}

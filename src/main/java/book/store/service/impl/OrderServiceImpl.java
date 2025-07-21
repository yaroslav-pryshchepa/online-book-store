package book.store.service.impl;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.exception.OrderProcessingException;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.Book;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.model.Status;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.OrderService;
import book.store.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderDto createOrder(AddOrderRequestDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ShoppingCart cart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id: " + user.getId()));
        ShoppingCartDto cartDto = shoppingCartMapper.toDto(cart);
        if (cart.getCartItems().isEmpty()) {
            throw new OrderProcessingException("Shopping cart is empty");
        }
        Order order = buildOrder(dto, user, cartDto);
        orderRepository.save(order);
        cart.clearCart();
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrders(Pageable pageable, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Page<Order> userOrders = orderRepository.findAllByUser(user, pageable);
        List<OrderDto> dtos = userOrders.getContent().stream()
                .map(orderMapper::toDto)
                .toList();
        return new PageImpl<>(dtos, pageable, userOrders.getTotalElements());
    }

    @Override
    public OrderDto updateStatus(Long id, UpdateStatusRequestDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found by id: "
                        + id));

        order.setStatus(Status.valueOf(dto.getStatus()));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id "
                        + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toOrderItemDto)
                .toList();
    }

    @Override
    public OrderItemDto getOrderItemById(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item not found with id " + itemId + " for order " + orderId));
        return orderItemMapper.toOrderItemDto(orderItem);
    }

    private Order buildOrder(AddOrderRequestDto dto, User user, ShoppingCartDto cartDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setShippingAddress(dto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());

        BigDecimal total = calculateTotal(cartDto);
        order.setTotal(total);

        Set<OrderItem> orderItems = buildOrderItems(cartDto, order);
        order.setOrderItems(orderItems);

        return order;
    }

    private BigDecimal calculateTotal(ShoppingCartDto cartDto) {
        return cartDto.getCartItems().stream()
                .map(cartItemDto -> {
                    Book book = bookRepository.findById(cartItemDto.getBookId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Book not found with id: " + cartItemDto.getBookId()));
                    return book.getPrice()
                            .multiply(BigDecimal.valueOf(cartItemDto.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> buildOrderItems(ShoppingCartDto cartDto, Order order) {
        return cartDto.getCartItems().stream()
                .map(cartItemDto -> {
                    Book book = bookRepository.findById(cartItemDto.getBookId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Book not found with id: " + cartItemDto.getBookId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(book);
                    orderItem.setQuantity(cartItemDto.getQuantity());
                    orderItem.setPrice(book.getPrice());
                    return orderItem;
                }).collect(Collectors.toSet());
    }
}

package book.store.service.impl;

import book.store.dto.order.AddOrderRequestDto;
import book.store.dto.order.OrderDto;
import book.store.dto.order.UpdateStatusRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderMapper;
import book.store.model.Book;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.Status;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.order.OrderRepository;
import book.store.repository.user.UserRepository;
import book.store.service.OrderService;
import book.store.service.ShoppingCartService;
import book.store.service.UserService;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto addOrder(AddOrderRequestDto dto) {
        Long currentUserId = userService.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: "
                        + userService.getCurrentUserId()));
        ShoppingCartDto cartDto = shoppingCartService.getShoppingCart();

        if (cartDto.getCartItems().isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setShippingAddress(dto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());

        BigDecimal total = cartDto.getCartItems().stream()
                .map(cartItemDto -> {
                    Book book = bookRepository.findById(cartItemDto.getBookId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Book not found with id: " + cartItemDto.getBookId()));
                    return book.getPrice()
                            .multiply(BigDecimal.valueOf(cartItemDto.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        Set<OrderItem> orderItems = cartDto.getCartItems().stream()
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

        order.setOrderItems(orderItems);

        orderRepository.save(order);

        shoppingCartService.clearShoppingCart(user);

        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrders(Pageable pageable) {
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: "
                        + userService.getCurrentUserId()));
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
}

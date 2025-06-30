package book.store.service.impl;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.repository.user.UserRepository;
import book.store.service.ShoppingCartService;
import book.store.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ShoppingCartDto getShoppingCart() {
        ShoppingCart cart = getOrCreateCart(userService.getCurrentUserId());
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(AddBookToCartRequestDto dto) {
        ShoppingCart cart = getOrCreateCart(userService.getCurrentUserId());
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found by id: " + dto.getBookId()));
        Optional<CartItem> existing = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(dto.getBookId()))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(dto.getQuantity());
            newItem.setShoppingCart(cart);
            cartItemRepository.save(newItem);
            cart.getCartItems().add(newItem);
        }

        return shoppingCartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto updateQuantity(Long cartItemId, QuantityDto quantity) {
        CartItem item = findUserCartItem(cartItemId);
        item.setQuantity(quantity.getQuantity());
        cartItemRepository.save(item);
        return shoppingCartMapper.toDto(cartRepository.save(item.getShoppingCart()));
    }

    @Override
    public ShoppingCartDto removeBookFromShoppingCart(Long cartItemId) {
        CartItem item = findUserCartItem(cartItemId);
        ShoppingCart cart = item.getShoppingCart();
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
        return shoppingCartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

    @Override
    public void clearShoppingCart(User user) {
        ShoppingCart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id: " + user.getId()));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private CartItem findUserCartItem(Long cartItemId) {
        ShoppingCart cart = getOrCreateCart(userService.getCurrentUserId());
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found by id: " + cartItemId));
    }

    private ShoppingCart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUser(userRepository.getReferenceById(userId));
                    return cartRepository.save(cart);
                });
    }
}

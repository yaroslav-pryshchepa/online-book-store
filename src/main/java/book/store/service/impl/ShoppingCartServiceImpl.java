package book.store.service.impl;

import book.store.dto.book.AddBookToCartRequestDto;
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
import book.store.service.ShoppingCartService;
import book.store.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserService userService;

    @Override
    public ShoppingCartDto getShoppingCart() {
        ShoppingCart cart = getOrCreateCart(userService.getCurrentUser());
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(AddBookToCartRequestDto dto) {
        ShoppingCart cart = getOrCreateCart(userService.getCurrentUser());
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

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
    public ShoppingCartDto updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return shoppingCartMapper.toDto(item.getShoppingCart());
    }

    @Override
    public ShoppingCartDto removeBookFromShoppingCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        ShoppingCart cart = item.getShoppingCart();
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        return shoppingCartMapper.toDto(cartRepository.save(cart));
    }

    private ShoppingCart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }
}


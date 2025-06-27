package book.store.service.impl;

import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.ShoppingCartManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagerServiceImpl implements ShoppingCartManagerService {
    private final ShoppingCartRepository cartRepository;

    public ShoppingCart createShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}

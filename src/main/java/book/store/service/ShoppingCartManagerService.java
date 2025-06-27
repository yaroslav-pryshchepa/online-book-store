package book.store.service;

import book.store.model.ShoppingCart;
import book.store.model.User;

public interface ShoppingCartManagerService {
    ShoppingCart createShoppingCart(User user);
}

package book.store.service;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto addBookToShoppingCart(AddBookToCartRequestDto dto);

    ShoppingCartDto updateQuantity(Long cartItemId, QuantityDto quantity);

    ShoppingCartDto removeBookFromShoppingCart(Long cartItemId);
}

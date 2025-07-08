package book.store.util;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.cartitem.CartItemDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ShoppingCartUtil {

    public static CartItem createCartItem1(ShoppingCart cart) {
        Book book = BookUtil.createBook(1L, null)
                .setTitle("Sapiens1")
                .setAuthor("Yuval Harari")
                .setIsbn("9780062316096")
                .setPrice(BigDecimal.valueOf(10.99));
        return new CartItem()
                .setId(1L)
                .setShoppingCart(cart)
                .setBook(book)
                .setQuantity(1);
    }

    public static CartItem createCartItem2(ShoppingCart cart) {
        Book book = BookUtil.createBook(2L, null)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("9780441013593")
                .setPrice(BigDecimal.valueOf(15.99));
        return new CartItem()
                .setId(2L)
                .setShoppingCart(cart)
                .setBook(book)
                .setQuantity(2);
    }

    public static CartItem createCartItem3(ShoppingCart cart) {
        Book book = BookUtil.createBook(3L, null)
                .setTitle("The Hobbit")
                .setAuthor("Yuval Tolkien")
                .setIsbn("9780547928227")
                .setPrice(BigDecimal.valueOf(20.99));
        return new CartItem()
                .setId(3L)
                .setShoppingCart(cart)
                .setBook(book)
                .setQuantity(1);
    }

    public static ShoppingCart createShoppingCartWithTwoItems() {
        User user = UserUtil.createUser();
        ShoppingCart cart = new ShoppingCart()
                .setId(1L)
                .setUser(user)
                .setCartItems(new HashSet<>());

        CartItem item1 = createCartItem1(cart);
        CartItem item2 = createCartItem2(cart);

        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        return cart;
    }

    public static ShoppingCart createShoppingCartWithThreeItems() {
        User user = UserUtil.createUser();
        ShoppingCart cart = new ShoppingCart()
                .setId(1L)
                .setUser(user)
                .setCartItems(new HashSet<>());

        CartItem item1 = createCartItem1(cart);
        CartItem item2 = createCartItem2(cart);
        CartItem item3 = createCartItem3(cart);

        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);
        cart.getCartItems().add(item3);

        return cart;
    }

    public static QuantityDto createQuantityDto(int quantity) {
        return new QuantityDto()
                .setQuantity(quantity);
    }

    public static AddBookToCartRequestDto createAddBookToCartRequestDto() {
        return new AddBookToCartRequestDto()
                .setBookId(3L)
                .setQuantity(1);
    }

    public static ShoppingCartDto createShoppingCartDto(Long id, Long userId) {
        CartItemDto cartItem1 = new CartItemDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Sapiens1")
                .setQuantity(1);

        CartItemDto cartItem2 = new CartItemDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Dune")
                .setQuantity(2);

        return new ShoppingCartDto()
                .setId(id)
                .setUserId(userId)
                .setCartItems(List.of(cartItem1, cartItem2));
    }

    public static CartItemDto cartItemDto1() {
        return new CartItemDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Sapiens1")
                .setQuantity(1);
    }

    public static CartItemDto cartItemDto2() {
        return new CartItemDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Dune")
                .setQuantity(2);
    }

    public static ShoppingCartDto createShoppingCartDtoAfterAddingBook(
            Long cartId, Long userId, Long newBookId, String newBookTitle, int newQuantity) {

        List<CartItemDto> items = new ArrayList<>();
        items.add(cartItemDto1());
        items.add(cartItemDto2());

        CartItemDto newItem = new CartItemDto()
                .setId(3L) // новий id, можна параметризувати, але для тесту так ок
                .setBookId(newBookId)
                .setBookTitle(newBookTitle)
                .setQuantity(newQuantity);

        items.add(newItem);

        return new ShoppingCartDto()
                .setId(cartId)
                .setUserId(userId)
                .setCartItems(items);
    }

    public static ShoppingCartDto createShoppingCartDtoAfterUpdatingQuantity(Long id, Long userId, Long cartItemId, int updatedQuantity) {
        CartItemDto cartItem1 = new CartItemDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Sapiens1")
                .setQuantity(updatedQuantity);

        CartItemDto cartItem2 = new CartItemDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Dune")
                .setQuantity(2);

        return new ShoppingCartDto()
                .setId(id)
                .setUserId(userId)
                .setCartItems(List.of(cartItem1, cartItem2));
    }

    public static ShoppingCartDto createShoppingCartDtoAfterRemovingItem(Long id, Long userId, Long removedCartItemId) {
        CartItemDto remainingItem = new CartItemDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Dune")
                .setQuantity(2);

        return new ShoppingCartDto()
                .setId(id)
                .setUserId(userId)
                .setCartItems(List.of(remainingItem));
    }
}

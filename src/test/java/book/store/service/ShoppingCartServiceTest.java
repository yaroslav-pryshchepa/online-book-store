package book.store.service;


import static book.store.util.BookUtil.createBook;
import static book.store.util.ShoppingCartUtil.createAddBookToCartRequestDto;
import static book.store.util.ShoppingCartUtil.createQuantityDto;
import static book.store.util.ShoppingCartUtil.createShoppingCartDto;
import static book.store.util.ShoppingCartUtil.createShoppingCartDtoAfterAddingBook;
import static book.store.util.ShoppingCartUtil.createShoppingCartDtoAfterRemovingItem;
import static book.store.util.ShoppingCartUtil.createShoppingCartDtoAfterUpdatingQuantity;
import static book.store.util.ShoppingCartUtil.createShoppingCartWithTwoItems;
import static book.store.util.UserUtil.createUser;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.repository.user.UserRepository;
import book.store.service.impl.ShoppingCartServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("getShoppingCart should return shopping cart DTO")
    void getShoppingCart_UserHasCart_ReturnsShoppingCartDto() {
        User user = createUser();
        ShoppingCart cart = createShoppingCartWithTwoItems();
        when(userService.getCurrentUserId()).thenReturn(user.getId());
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        ShoppingCartDto expectedDto = createShoppingCartDto(1L, 1L);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);
        ShoppingCartDto actualDto = shoppingCartService.getShoppingCart();
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId(user.getId());
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("addBookToShoppingCart should add new book to cart")
    void addBookToShoppingCart_NewBook_AddsBookToCartAndReturnsUpdatedDto() {
        User user = createUser();
        ShoppingCart cart = createShoppingCartWithTwoItems();
        AddBookToCartRequestDto requestDto = createAddBookToCartRequestDto();
        when(userService.getCurrentUserId()).thenReturn(user.getId());
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        var book3 = createBook(3L, null)
                .setTitle("The Hobbit")
                .setAuthor("Yuval Tolkien")
                .setIsbn("9780547928227")
                .setPrice(java.math.BigDecimal.valueOf(20.99));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book3));
        doAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            item.setId(3L);
            cart.getCartItems().add(item);
            return item;
        }).when(cartItemRepository).save(any(CartItem.class));
        when(cartRepository.save(cart)).thenReturn(cart);
        ShoppingCartDto expectedDto = createShoppingCartDtoAfterAddingBook(
                1L, 1L, 3L, "The Hobbit", 1);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);
        ShoppingCartDto actualDto = shoppingCartService.addBookToShoppingCart(requestDto);
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId(user.getId());
        verify(bookRepository).findById(requestDto.getBookId());
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("updateQuantity should update cart item quantity")
    void updateQuantity_ValidCartItemId_UpdatesQuantityAndReturnsUpdatedDto() {
        User user = createUser();
        ShoppingCart cart = createShoppingCartWithTwoItems();
        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(1L))
                .findFirst().orElseThrow();
        QuantityDto quantityDto = createQuantityDto(3);
        when(userService.getCurrentUserId()).thenReturn(user.getId());
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(itemToUpdate.getId(), cart.getId()))
                .thenReturn(Optional.of(itemToUpdate));
        doAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            return item;
        }).when(cartItemRepository).save(any(CartItem.class));
        when(cartRepository.save(cart)).thenReturn(cart);
        ShoppingCartDto expectedDto = createShoppingCartDtoAfterUpdatingQuantity(
                1L, 1L, 1L, quantityDto.getQuantity());
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);

        ShoppingCartDto actualDto = shoppingCartService.updateQuantity(itemToUpdate.getId(),
                quantityDto);
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId(user.getId());
        verify(cartItemRepository).findByIdAndShoppingCartId(itemToUpdate.getId(), cart.getId());
        verify(cartItemRepository).save(itemToUpdate);
        verify(cartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("removeBookFromShoppingCart should remove item and update cart")
    void removeBookFromShoppingCart_ValidCartItemId_RemovesItemAndReturnsUpdatedDto() {
        User user = createUser();
        ShoppingCart cart = createShoppingCartWithTwoItems();
        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(1L))
                .findFirst().orElseThrow();
        when(userService.getCurrentUserId()).thenReturn(user.getId());
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(itemToRemove.getId(), cart.getId()))
                .thenReturn(Optional.of(itemToRemove));
        doNothing().when(cartItemRepository).delete(itemToRemove);
        doAnswer(invocation -> {
            cart.getCartItems().remove(itemToRemove);
            return cart;
        }).when(cartRepository).save(cart);
        ShoppingCartDto expectedDto = createShoppingCartDtoAfterRemovingItem(
                1L, 1L, itemToRemove.getId());
        when(shoppingCartMapper.toDto(cart)).thenReturn(expectedDto);
        ShoppingCartDto actualDto = shoppingCartService.removeBookFromShoppingCart(
                itemToRemove.getId());
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId(user.getId());
        verify(cartItemRepository).findByIdAndShoppingCartId(itemToRemove.getId(), cart.getId());
        verify(cartItemRepository).delete(itemToRemove);
        verify(cartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("addBookToShoppingCart should throw when book not found")
    void addBookToShoppingCart_BookNotFound_ThrowsEntityNotFoundException() {
        User user = createUser();
        AddBookToCartRequestDto requestDto = createAddBookToCartRequestDto();
        when(userService.getCurrentUserId()).thenReturn(user.getId());
        when(cartRepository.findByUserId(user.getId())).thenReturn(
                Optional.of(createShoppingCartWithTwoItems()));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToShoppingCart(requestDto));
        assertEquals("Book not found by id: " + requestDto.getBookId(), ex.getMessage());
        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId(user.getId());
        verify(bookRepository).findById(requestDto.getBookId());
        verifyNoMoreInteractions(cartItemRepository, cartRepository, shoppingCartMapper);
    }
}


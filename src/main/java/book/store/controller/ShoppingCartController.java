package book.store.controller;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get user's shopping cart", description = "Get user's shopping cart")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid AddBookToCartRequestDto addBookRequestDto) {
        return shoppingCartService.addBookToShoppingCart(addBookRequestDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("items/{cartItemId}")
    @Operation(summary = "Update quantity of a book",
            description = "Update quantity of a book in the shopping cart")
    public ShoppingCartDto updateQuantity(@PathVariable Long cartItemId,
            @RequestBody @Valid QuantityDto quantity) {
        return shoppingCartService.updateQuantity(cartItemId, quantity);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("items/{cartItemId}")
    @Operation(summary = "Remove book from shopping cart",
            description = "Remove a book from the shopping cart")
    public ShoppingCartDto removeBookFromShoppingCart(@PathVariable Long cartItemId) {
        return shoppingCartService.removeBookFromShoppingCart(cartItemId);
    }
}

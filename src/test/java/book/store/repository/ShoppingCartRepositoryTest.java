package book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.model.ShoppingCart;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find shopping cart by user id returns correct cart")
    @Sql(scripts = {
            "classpath:database/delete-books-and-categories-from-db.sql",
            "classpath:database/delete-shopping-cart.sql",
            "classpath:database/delete-users-and-roles.sql",
            "classpath:database/insert-books-and-categories.sql",
            "classpath:database/insert-users-and-roles.sql",
            "classpath:database/insert-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/delete-shopping-cart.sql",
            "classpath:database/delete-books-and-categories-from-db.sql",
            "classpath:database/delete-users-and-roles.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ReturnsCorrectCart() {
        Long userId = 1L;
        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUserId(userId);
        assertTrue(optionalCart.isPresent());
        ShoppingCart cart = optionalCart.get();
        assertEquals(userId, cart.getUser().getId());
        assertEquals(2, cart.getCartItems().size());
        List<String> actual = cart.getCartItems().stream()
                .map(item -> item.getBook().getTitle())
                .sorted()
                .toList();
        List<String> expected = List.of("Dune", "Sapiens1").stream()
                .sorted()
                .toList();
        assertEquals(expected, actual);
    }

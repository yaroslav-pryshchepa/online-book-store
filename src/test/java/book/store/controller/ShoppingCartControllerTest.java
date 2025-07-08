package book.store.controller;

import static book.store.util.ShoppingCartUtil.createAddBookToCartRequestDto;
import static book.store.util.ShoppingCartUtil.createQuantityDto;
import static book.store.util.ShoppingCartUtil.createShoppingCartWithThreeItems;
import static book.store.util.ShoppingCartUtil.createShoppingCartWithTwoItems;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.AddBookToCartRequestDto;
import book.store.dto.cartitem.CartItemDto;
import book.store.dto.shoppingcart.QuantityDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.ShoppingCart;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "/database/delete-users-and-roles.sql",
        "/database/insert-users-and-roles.sql"
},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/database/delete-users-and-roles.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws Exception {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/insert-books-and-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/insert-shopping-cart.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-shopping-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-books-and-categories-from-db.sql")
            );
        }
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Get shopping cart and validate content")
    void getShoppingCart_AuthenticatedUser_ReturnsShoppingCart() throws Exception {
        ShoppingCartDto expected = shoppingCartMapper.toDto(createShoppingCartWithTwoItems());
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        var expectedItems = new ArrayList<>(expected.getCartItems());
        var actualItems = new ArrayList<>(actual.getCartItems());
        expectedItems.sort(Comparator.comparing(CartItemDto::getBookId));
        actualItems.sort(Comparator.comparing(CartItemDto::getBookId));
        expected.setCartItems(expectedItems);
        actual.setCartItems(actualItems);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Add book to shopping cart and validate updated cart")
    void addBookToShoppingCart_ValidRequest_ReturnsUpdatedCart() throws Exception {
        AddBookToCartRequestDto requestDto = createAddBookToCartRequestDto();
        ShoppingCartDto expected = shoppingCartMapper.toDto(createShoppingCartWithThreeItems());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        ArrayList<CartItemDto> expectedItems = new ArrayList<>(
                expected.getCartItems());
        ArrayList<CartItemDto> actualItems = new ArrayList<>(actual.getCartItems());
        expectedItems.sort(Comparator.comparing(CartItemDto::getBookId));
        actualItems.sort(Comparator.comparing(CartItemDto::getBookId));
        expected.setCartItems(expectedItems);
        actual.setCartItems(actualItems);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Update quantity and validate updated cart")
    void updateQuantity_ValidCartItemId_ReturnsUpdatedCart() throws Exception {
        QuantityDto requestDto = createQuantityDto(3);
        ShoppingCartDto baseCart = shoppingCartMapper.toDto(createShoppingCartWithTwoItems());
        baseCart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(1L))
                .findFirst()
                .ifPresent(ci -> ci.setQuantity(3));
        ShoppingCartDto expected = shoppingCartMapper.toDto(createShoppingCartWithTwoItems());
        expected.getCartItems().stream()
                .filter(ci -> ci.getId().equals(1L))
                .findFirst()
                .ifPresent(ci -> ci.setQuantity(3));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/cart/items/{cartItemId}", 1L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        ArrayList<CartItemDto> expectedItems = new ArrayList<>(expected.getCartItems());
        ArrayList<CartItemDto> actualItems = new ArrayList<>(actual.getCartItems());
        expectedItems.sort(Comparator.comparing(CartItemDto::getBookId));
        actualItems.sort(Comparator.comparing(CartItemDto::getBookId));

        expected.setCartItems(expectedItems);
        actual.setCartItems(actualItems);

        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Remove book from shopping cart and validate updated cart")
    void removeBookFromShoppingCart_ValidCartItemId_RemovesItemAndReturnsCart() throws Exception {
        ShoppingCart cartAfterRemoval = createShoppingCartWithTwoItems();
        cartAfterRemoval.getCartItems().removeIf(ci -> ci.getId().equals(1L));
        ShoppingCartDto expected = shoppingCartMapper.toDto(cartAfterRemoval);
        MvcResult result = mockMvc.perform(delete("/cart/items/{cartItemId}", 1L))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get shopping cart without authentication returns 401 Unauthorized")
    void getShoppingCart_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Add book to shopping cart with invalid request returns 400 Bad Request")
    void addBookToShoppingCart_InvalidRequest_ReturnsBadRequest() throws Exception {
        AddBookToCartRequestDto invalidRequestDto = new AddBookToCartRequestDto();
        invalidRequestDto.setBookId(null);
        invalidRequestDto.setQuantity(0);

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Update quantity with invalid cartItemId returns 404 Not Found")
    void updateQuantity_InvalidCartItemId_ReturnsNotFound() throws Exception {
        QuantityDto requestDto = createQuantityDto(3);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cart/items/{cartItemId}", 9999)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithUserDetails("test@example.com")
    @Test
    @DisplayName("Remove book from shopping cart with invalid cartItemId returns 404 Not Found")
    void removeBookFromShoppingCart_InvalidCartItemId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/cart/items/{cartItemId}", 9999))
                .andExpect(status().isNotFound());
    }
}

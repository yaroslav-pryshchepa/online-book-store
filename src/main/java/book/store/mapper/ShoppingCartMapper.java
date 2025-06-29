package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.cartitem.CartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toCartItemDto(CartItem cartItem);
}

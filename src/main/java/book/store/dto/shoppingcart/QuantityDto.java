package book.store.dto.shoppingcart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class QuantityDto {
    @Positive
    private int quantity;
}

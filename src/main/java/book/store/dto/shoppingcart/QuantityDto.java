package book.store.dto.shoppingcart;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QuantityDto {
    @Positive
    private int quantity;
}

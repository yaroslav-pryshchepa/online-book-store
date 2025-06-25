package book.store.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemRequestDto {
    @Min(value = 1)
    private int quantity;
}

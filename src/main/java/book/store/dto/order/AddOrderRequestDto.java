package book.store.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddOrderRequestDto {
    @NotBlank
    private String shippingAddress;
}

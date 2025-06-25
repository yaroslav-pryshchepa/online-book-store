package book.store.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookToCartRequestDto {
    @NotNull
    private Long bookId;
    @Min(value = 1)
    private int quantity;
}

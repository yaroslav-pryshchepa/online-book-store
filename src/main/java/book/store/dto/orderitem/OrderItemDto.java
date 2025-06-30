package book.store.dto.orderitem;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDto {
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private BigDecimal price;
}

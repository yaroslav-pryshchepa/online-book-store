package book.store.dto.orderitem;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long bookId;
    private int quantity;
}
